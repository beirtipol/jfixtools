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

package tree;

import com.beirtipol.jfixtools.ui.tree.FIXMessageTreeNode;
import com.beirtipol.jfixtools.ui.tree.FIXTreeNode;
import org.junit.jupiter.api.Test;
import quickfix.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldMapTreeNodeTest {
    @Test
    public void parseFixMessageToTree_checkOrderID() throws URISyntaxException, ConfigError, InvalidMessage, IOException {
        URL resource = getClass().getClassLoader().getResource("./fix/NewOrderSingle.fix");
        String text = Files.readString(Paths.get(resource.toURI()));
        DataDictionary dataDictionary = new DataDictionary("FIX44.xml");
        Message message = MessageUtils.parse(new DefaultMessageFactory(), dataDictionary, text);
        FIXMessageTreeNode tree = new FIXMessageTreeNode(dataDictionary, message);

        List<FIXTreeNode> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(3);
        assertThat(children.get(1).getChildren().stream().filter(node -> "11".equals(node.getTag())).findFirst().get().getValue()).isEqualTo("636730640278898634");
    }
}
