package rules;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created by Nixy on 10.04.2015.
 */
@Component
public class LinearConclusionSet implements ConclusionSetFabric {

    @Override
    public Deque<Conclusion> makeSet(List<Conclusion> conclusions, int tail) {
        Deque<Conclusion> result = new ArrayDeque<Conclusion>(conclusions);
        return result;
    }
}
