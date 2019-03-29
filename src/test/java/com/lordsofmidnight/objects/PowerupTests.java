package com.lordsofmidnight.objects;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.powerUps.Invincible;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.objects.powerUps.Rocket;
import com.lordsofmidnight.objects.powerUps.Speed;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

public class PowerupTests {

  @Test
  void effectPowerups() {
    ConcurrentHashMap<UUID, PowerUp> activePowerups = new ConcurrentHashMap<>();
    int[][] map = new int[][]{{1, 1}, {0, 0}};
    PointMap<Pellet> pellets = new PointMap<Pellet>(new Map(map));
    Entity entity = new Entity(false, 0, new Point(0, 0));
    Entity[] agents = new Entity[]{entity};
    PowerUp powerUp = new Speed();
    AudioController controller = new AudioController(1);
    powerUp.use(entity, activePowerups, pellets, agents, controller);
    assert (entity.isSpeeding());
    powerUp = new Invincible();
    powerUp.use(entity, activePowerups, pellets, agents, controller);
    assert (entity.isInvincible());
  }

  @Test
  void rocketTest() {
    AudioController controller = new AudioController(1);
    ConcurrentHashMap<UUID, PowerUp> activePowerups = new ConcurrentHashMap<>();
    int[][] map = new int[][]{{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0}};
    PointMap<Pellet> pellets = new PointMap<Pellet>(new Map(map));
    Entity entity1 = new Entity(false, 0, new Point(3, 3));
    Entity entity2 = new Entity(false, 1, new Point(4, 4));
    entity1.setScore(200);
    entity2.setScore(2);
    Entity[] agents = new Entity[]{entity1, entity2};
    Rocket rocket = new Rocket();
    rocket.use(entity2, activePowerups, pellets, agents, controller);
    assert (rocket.getTargeted() == entity1);
  }
}
