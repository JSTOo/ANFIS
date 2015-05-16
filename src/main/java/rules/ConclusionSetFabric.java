package rules;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;

/**
 * Created by Nixy on 10.04.2015.
 */
public interface ConclusionSetFabric extends Serializable {
    Deque<Conclusion> makeSet(List<Conclusion> conclusions,int tail);
}
