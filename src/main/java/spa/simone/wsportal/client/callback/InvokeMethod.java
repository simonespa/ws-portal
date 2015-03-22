/*
 * Copyright © 2010
 *
 * This file is part of "WS Portal" web application.
 *
 * WS Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WS Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WS Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unical.inf.wsportal.client.callback;

import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import it.unical.inf.wsportal.client.WSPortal;
import it.unical.inf.wsportal.client.view.OutputPortlet;

/**
 *
 * @author Simone Spaccarotella {spa.simone@gmail.com}, Carmine Dodaro {carminedodaro@gmail.com}
 */
public class InvokeMethod extends Callback {

    private String heading;

    public InvokeMethod(String heading) {
        this.heading = heading;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() == Response.SC_OK) {
            OutputPortlet portlet = new OutputPortlet(heading);
            portlet.addText(response.getText());
            WSPortal.getOutputPortletContainer().add(portlet);
        }
    }
}
