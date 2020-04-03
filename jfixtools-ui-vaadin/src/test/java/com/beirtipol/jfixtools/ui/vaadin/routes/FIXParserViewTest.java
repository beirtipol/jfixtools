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

package com.beirtipol.jfixtools.ui.vaadin.routes;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NavigationState;
import com.vaadin.flow.router.Router;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FIXParserViewTest {

    private static Routes routes;

    @BeforeAll
    public static void createRoutes() {
        routes = new Routes().autoDiscoverViews("com.beirtipol.jfixtools.ui.vaadin.routes");
    }

    @BeforeEach
    public void setupVaadin() {
        MockVaadin.setup(routes);
    }

    @Test
    public void replacePipeWithSOH() {
//        Router router = UI.getCurrent().getRouter();
//        Optional<NavigationState> state = router.resolveNavigationTarget(new Location("parser"));
//        Stream<Component> children = UI.getCurrent().getChildren();
//        final FIXParserView main = (FIXParserView) children.findFirst().get();
//        assertEquals(2, main.getChildren().count());
//        TextAreaElement inputArea = $(TextAreaElement.class).id("textArea_input");
//        inputArea.setValue("8=FIX.4.4|9=72|35=A|34=5|49=Initiator|52=20200822-09:10:51.144|56=Acceptor|98=0|108=30|10=224|");
//        $(ButtonElement.class).id("btn_replacepipe").click();
//        Assert.assertEquals("8=FIX.4.4\u00019=72\u000135=A\u000134=5\u000149=Initiator\u000152=20200822-09:10:51.144\u000156=Acceptor\u000198=0\u0001108=30\u000110=224\u0001",
//                inputArea.getValue());
    }

}