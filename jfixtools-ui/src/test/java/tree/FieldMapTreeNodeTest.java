package tree;

import com.beirtipol.jfixtools.ui.tree.FIXMessageTreeNode;
import com.beirtipol.jfixtools.ui.tree.FIXTreeNode;
import com.beirtipol.jfixtools.ui.tree.ITreeNode;
import org.assertj.core.api.Assertions;
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
