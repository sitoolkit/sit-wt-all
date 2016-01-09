package org.sitoolkit.wt.domain.pageload;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;

import java.util.SortedMap;
import java.util.TreeMap;

public class PageContext {

    private String title;

    private String url;

    private SortedMap<ElementPosition, TestStep> map = new TreeMap<>(new PointComparator());

    private class PointComparator implements Comparator<ElementPosition> {

        @Override
        public int compare(ElementPosition o1, ElementPosition o2) {
            if (o1 == null | o2 == null) {
                return 0;
            }
            int dy = o1.getY() - o2.getY();

            if (dy != 0) {
                return dy;
            }

            int dx = o1.getX() - o2.getX();

            if (dx != 0) {
                return dx;
            }

            return o1.hashCode() - o2.hashCode();
        }
    }

    public void add(ElementPosition pos, TestStep step) {
        map.put(pos, step);
    }

    public List<TestStep> asList() {
        List<TestStep> list = new ArrayList<>();

        int stepNo = 1;
        for (Entry<ElementPosition, TestStep> entry : map.entrySet()) {
            TestStep step = entry.getValue();
            step.setNo(Integer.toString(stepNo++));
            list.add(step);
        }

        return list;
    }

    public boolean containsName(String name) {
        for (Entry<ElementPosition, TestStep> entry : map.entrySet()) {
            if (entry.getValue().getLocator().getTypeVo() == Locator.Type.name
                    && name.equals(entry.getValue().getLocator().getValue())) {
                return true;
            }
        }
        return false;
    }

    public TestStep create() {
        TestStep step = new TestStep();
        step.setCurrentCaseNo("001");
        step.setTestData("001", "y");

        return step;
    }

    public TestStep registTestStep(ElementPosition pos) {
        TestStep step = create();
        add(pos, step);

        return step;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
