/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.AbstractAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.*;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;

public class RunReportAdapter extends AbstractAdapter {

    private final MultivaluedMap<String, String> params;
    private final String reportUnitUri;
    private final ReportOutputFormat format;
    private String[] pages;

    private RunReportAdapter(SessionStorage sessionStorage, String reportUnitUri, ReportOutputFormat format) {
        super(sessionStorage);
        this.params = new MultivaluedHashMap<String, String>();
        this.reportUnitUri = reportUnitUri;
        this.format = format;
    }

    public RunReportAdapter(SessionStorage sessionStorage, String reportUnitUri,
                            ReportOutputFormat format, Integer[] pages) {
        this(sessionStorage, reportUnitUri, format);
        this.pages = toStringArray(pages);
    }

    public RunReportAdapter(SessionStorage sessionStorage, String reportUnitUri,
                            ReportOutputFormat format, PageRange range) {
        this(sessionStorage, reportUnitUri, format);
        this.pages = new String[]{range.getRange()};
    }

    public RunReportAdapter parameter(String name, String value) {
        params.add(name, value);
        return this;
    }

    public OperationResult<InputStream> run() {
        JerseyRequest<InputStream> builder = prepareRunRequest();
        return builder.get();
    }

    public <R> RequestExecution asyncRun(final Callback<OperationResult<InputStream>, R> callback) {
        final JerseyRequest<InputStream> builder = prepareRunRequest();

        RequestExecution task = new RequestExecution(new Runnable() {
            @Override
            public void run() {
                callback.execute(builder.get());
            }
        });

        ThreadPoolUtil.runAsynchronously(task);
        return task;
    }

    private JerseyRequest<InputStream> prepareRunRequest(){
        JerseyRequest<InputStream> builder =
                buildRequest(sessionStorage, InputStream.class,
                        new String[]{"/reports", reportUnitUri + "." + format.toString().toLowerCase()}, new RunReportErrorHandler());
        builder.addParams(params);

        if (pages != null && pages.length > 0) {
            if (pages.length == 1) {
                final Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)$");
                final Matcher matcher = pattern.matcher(pages[0]);
                if (matcher.matches()) {
                    builder.addParam("pages", pages[0]);
                } else {
                    builder.addParam("page", pages[0]);
                }
            }
            if (pages.length > 1)
                builder.addParam("pages", pages);
        }
        return builder;
    }

    private String[] toStringArray(Integer[] ints) {
        String[] strings = new String[ints.length];
        for (int i = 0; i < ints.length; i++)
            strings[i] = ints[i].toString();
        return strings;
    }

}