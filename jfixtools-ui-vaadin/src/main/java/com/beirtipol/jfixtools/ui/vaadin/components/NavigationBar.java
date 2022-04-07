/*
 * Copyright (C) 2020  https://github.com/beirtipol
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.beirtipol.jfixtools.ui.vaadin.components;

import com.beirtipol.jfixtools.ui.vaadin.routes.FIXHomeView;
import com.beirtipol.jfixtools.ui.vaadin.routes.FIXLogsView;
import com.beirtipol.jfixtools.ui.vaadin.routes.FIXParserView;
import com.beirtipol.jfixtools.ui.vaadin.routes.FIXRepositoryView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;


public class NavigationBar extends HorizontalLayout {
    public NavigationBar() {
        add(new RouterLink("Home", FIXHomeView.class));
        add(new RouterLink("Logs", FIXLogsView.class));
        add(new RouterLink("Parser", FIXParserView.class));
        add(new RouterLink("Repository", FIXRepositoryView.class));
    }
}
