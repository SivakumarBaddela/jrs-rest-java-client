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

package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs.calendar;

import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.AbstractAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.*;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.ReportJobCalendar;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.calendars.Calendar;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;

public class SingleCalendarOperationsAdapter extends AbstractAdapter {

    private final String calendarName;
    private final MultivaluedMap<String, String> params;

    public SingleCalendarOperationsAdapter(SessionStorage sessionStorage, String calendarName) {
        super(sessionStorage);
        this.calendarName = calendarName;
        params = new MultivaluedHashMap<String, String>();
    }

    public SingleCalendarOperationsAdapter parameter(CalendarParameter parameter, String value){
        params.add(parameter.getName(), value);
        return this;
    }

    public OperationResult<ReportJobCalendar> get(){
        return buildRequest(sessionStorage, ReportJobCalendar.class, new String[]{"/jobs", "/calendars", calendarName})
                .get();
    }

    public <R> RequestExecution asyncGet(final Callback<OperationResult<ReportJobCalendar>, R> callback) {
        final JerseyRequest<ReportJobCalendar> builder =
                buildRequest(sessionStorage, ReportJobCalendar.class, new String[]{"/jobs", "/calendars", calendarName});

        RequestExecution task = new RequestExecution(new Runnable() {
            @Override
            public void run() {
                callback.execute(builder.get());
            }
        });

        ThreadPoolUtil.runAsynchronously(task);
        return task;
    }

    public OperationResult delete(){
        return buildRequest(sessionStorage, Object.class, new String[]{"/jobs", "/calendars", calendarName})
                .delete();
    }

    public <R> RequestExecution asyncDelete(final Callback<OperationResult, R> callback) {
        final JerseyRequest builder =
                buildRequest(sessionStorage, Object.class, new String[]{"/jobs", "/calendars", calendarName});

        RequestExecution task = new RequestExecution(new Runnable() {
            @Override
            public void run() {
                callback.execute(builder.delete());
            }
        });

        ThreadPoolUtil.runAsynchronously(task);
        return task;
    }

    public OperationResult<ReportJobCalendar> createNew(Calendar calendarDescriptor){
        JerseyRequest<ReportJobCalendar> builder =
                buildRequest(sessionStorage, ReportJobCalendar.class, new String[]{"/jobs", "/calendars", calendarName});
        builder.addParams(params);

        return builder.put(calendarDescriptor);
    }

    public <R> RequestExecution asyncCreateNew(final Calendar calendarDescriptor, final Callback<OperationResult<ReportJobCalendar>, R> callback) {
        final JerseyRequest<ReportJobCalendar> builder =
                buildRequest(sessionStorage, ReportJobCalendar.class, new String[]{"/jobs", "/calendars", calendarName});
        builder.addParams(params);

        RequestExecution task = new RequestExecution(new Runnable() {
            @Override
            public void run() {
                callback.execute(builder.put(calendarDescriptor));
            }
        });

        ThreadPoolUtil.runAsynchronously(task);
        return task;
    }

}
