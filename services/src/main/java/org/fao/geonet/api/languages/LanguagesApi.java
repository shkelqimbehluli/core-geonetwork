/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

package org.fao.geonet.api.languages;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.api.API;
import org.fao.geonet.api.ApiUtils;
import org.fao.geonet.domain.Language;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.exceptions.ResourceNotFoundEx;
import org.fao.geonet.kernel.GeonetworkDataDirectory;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.repository.LanguageRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jeeves.server.context.ServiceContext;
import jeeves.server.dispatchers.ServiceManager;
import springfox.documentation.annotations.ApiIgnore;

@RequestMapping(value = {
    "/api/languages",
    "/api/" + API.VERSION_0_1 +
        "/languages"
})
@Api(value = "languages",
    tags = "languages",
    description = "Languages operations")
@Controller("languages")
public class LanguagesApi {

    @ApiOperation(
        value = "Get languages",
        notes = "Languages for the application (having translations in the database.",
        nickname = "getLanguages")
    @RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Language> getLanguages() throws Exception {
        return ApplicationContextHolder.get().getBean(LanguageRepository.class).findAll();
    }


    @ApiOperation(
        value = "Add a language",
        notes = "Add all translations from all *Desc tables in the database.",
        nickname = "addLanguage")
    @RequestMapping(
        value = "/{langCode}",
        method = RequestMethod.PUT)
    @PreAuthorize("hasRole('Administrator')")
    @ResponseBody
    public ResponseEntity addLanguages(
        @ApiParam(value = "ISO 3 letter code",
            required = true)
        @PathVariable
            String langCode,
        @ApiIgnore
            HttpSession session,
        @ApiIgnore
            HttpServletRequest request
    ) throws ResourceNotFoundEx, IOException {

        ConfigurableApplicationContext applicationContext = ApplicationContextHolder.get();
        ServiceManager serviceManager = applicationContext.getBean(ServiceManager.class);

        LanguageRepository languageRepository = applicationContext.getBean(LanguageRepository.class);
        Language lang = languageRepository.findOne(langCode);
        if (lang == null) {
            GeonetworkDataDirectory dataDirectory = applicationContext.getBean(GeonetworkDataDirectory.class);
            String languageDataFile = "loc-" + langCode + "-default.sql";
            Path templateFile = dataDirectory.getWebappDir().resolve("WEB-INF")
                .resolve("classes").resolve("setup").resolve("sql").resolve("data")
                .resolve(languageDataFile);
            if (Files.exists(templateFile)) {
                List<String> data = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(templateFile.toFile()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        data.add(line);
                    }
                }
                if (data.size() > 0) {
                    ServiceContext context = ApiUtils.createServiceContext(request);
                    Lib.db.runSQL(context, data);
                    return new ResponseEntity(HttpStatus.CREATED);
                }
            }
            throw new ResourceNotFoundEx(String.format(
                "Language data file '%s' not found in classes/setup/sql/data.", languageDataFile
            ));
        } else {
            throw new RuntimeException(String.format(
                "Language '%s' already available.", lang.getId()
            ));
        }
    }

    @ApiOperation(
        value = "Delete a language",
        notes = "Delete all translations from all *Desc tables in the database.",
        nickname = "addLanguage")
    @RequestMapping(
        value = "/{langCode}",
        method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('Administrator')")
    @ResponseBody
    public ResponseEntity deleteLanguages(
        @ApiParam(value = "ISO 3 letter code",
            required = true)
        @PathVariable
            String langCode,
            HttpSession session,
            HttpServletRequest request
    ) throws ResourceNotFoundEx, IOException {
        // TODO: null context

        ConfigurableApplicationContext applicationContext = ApplicationContextHolder.get();

        LanguageRepository languageRepository = applicationContext.getBean(LanguageRepository.class);
        Language lang = languageRepository.findOne(langCode);
        if (lang == null) {
            throw new ResourceNotFoundEx(String.format("Language '%s' not found.", langCode));
        } else {
            GeonetworkDataDirectory dataDirectory = applicationContext.getBean(GeonetworkDataDirectory.class);

            final String LANGUAGE_DELETE_SQL = "language-delete.sql";

            Path templateFile = dataDirectory.getWebappDir().resolve("WEB-INF")
                .resolve("classes").resolve("setup").resolve("sql").resolve("template")
                .resolve(LANGUAGE_DELETE_SQL);
            if (Files.exists(templateFile)) {
                List<String> data = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(templateFile.toFile()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        data.add(String.format(line, lang.getId()));
                    }
                }
                if (data.size() > 0) {
                    ServiceContext context = ApiUtils.createServiceContext(request);
                    Lib.db.runSQL(context, data);
                    return new ResponseEntity(HttpStatus.NO_CONTENT);
                }
            }
            throw new ResourceNotFoundEx(String.format(
                "Template file '%s' not found in classes/setup/sql/template.", LANGUAGE_DELETE_SQL
            ));
        }
    }
}