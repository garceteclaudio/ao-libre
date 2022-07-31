package component.entity.character.states;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Immobile extends Component implements Serializable {
    public Immobile() {
    }

}
