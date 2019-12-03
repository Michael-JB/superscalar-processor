package instruction;

import java.util.ArrayList;
import java.util.List;

public class TagGenerator {

  private List<Integer> assignedTagValues = new ArrayList<Integer>();

  public TagGenerator() {}

  public void retireTag(Tag tag) {
    if (assignedTagValues.contains(tag.getValue())) {
      assignedTagValues.remove(tag.getValue());
    }
  }

  public Tag generateTag() {
    int i = 0;
    while (assignedTagValues.contains(i)) {
      i++;
    }
    assignedTagValues.add(i);
    return new Tag(i);
  }

}