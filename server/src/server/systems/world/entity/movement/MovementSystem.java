package server.systems.world.entity.movement;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.movement.Destination;
import component.physics.AOPhysics;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.ServerSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.network.movement.MovementNotification;
import shared.network.movement.MovementResponse;
import shared.util.EntityUpdateBuilder;

import java.util.Optional;

// TODO refactor: make active system processing entities with Moving component
@Wire
public class MovementSystem extends PassiveSystem {

    // Injected systems
    private ServerSystem serverSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private MapSystem mapSystem;
    private EntityUpdateSystem entityUpdateSystem;

    public void move(int connectionId, int movementIndex, int requestNumber) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);

        // Obtiene la entidad a evaluar.
        E player = E.E(playerId);

        // Se fija donde esta el player y hacia donde quiere ir
        WorldUtils worldUtils = WorldUtils.WorldUtils(world);
        AOPhysics.Movement movement = AOPhysics.Movement.values()[movementIndex];
        player.headingCurrent(worldUtils.getHeading(movement));
        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, movement);
        Map map = mapSystem.getMap(nextPos.map);

        // Hay un bloqueo?
        boolean blocked = mapSystem.getHelper().isBlocked(map, nextPos);

        // Hay un jugador en esta pos?
        boolean occupied = mapSystem.getHelper().hasEntity(mapSystem.getNearEntities(playerId), nextPos);

        // Obtengo prox. pos disponible
        if (!(player.hasImmobile() || blocked || occupied)) {
            Tile tile = mapSystem.getMap(nextPos.map).getTile(nextPos.x, nextPos.y);
            WorldPosition tileExit = tile.getTileExit();
            if (tileExit != null) {
                Log.info("Moving to exit tile: " + tileExit);
                nextPos = new WorldPos(tileExit.getX(), tileExit.getY(), tileExit.getMap());
            }
            player
                    .worldPosMap(nextPos.map)
                    .worldPosX(nextPos.x)
                    .worldPosY(nextPos.y);
        } else {
            nextPos = oldPos;
        }

        mapSystem.movePlayer(playerId, Optional.of(oldPos));

        // notify near users
        if (!nextPos.equals(oldPos)) {
            if (nextPos.map != oldPos.map) {
                entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(E.E(playerId).getWorldPos()).build(), UpdateTo.NEAR);
            } else {
                // TODO convert notification into component.entity update
                worldEntitiesSystem.notifyToNearEntities(playerId, new MovementNotification(playerId, new Destination(nextPos, movement.ordinal())));
            }
        } else {
            entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(player.getHeading()).build(), UpdateTo.NEAR);
        }

        // notify user
        serverSystem.sendTo(connectionId, new MovementResponse(requestNumber, nextPos));
    }

}
