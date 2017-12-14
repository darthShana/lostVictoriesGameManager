package com.lostVictories.dao;


import java.util.UUID;


import com.lostVictories.model.GameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.stream.Collectors;

public class GameDAO {

    private static Logger log = LoggerFactory.getLogger(GameDAO.class);

    private final JedisPool jedisPool;
    private final String characterStatus;
    private final String characterLocation;

    public GameDAO(String nameSpace, JedisPool jedisPool) {
        this.characterStatus = nameSpace+".characterStatus";
        this.characterLocation = nameSpace+".characterLocation";
        this.jedisPool = jedisPool;
    }

    private Set<GameCharacter> getAllCharacters(Jedis jedis) {
        List<GeoRadiusResponse> mapResponse = jedis.georadius(this.characterLocation, 0, 0, 1000000, GeoUnit.KM);
        return mapResponse.stream().map(r->new GameCharacter(r.getMemberByString(), jedis.hgetAll(characterStatus + "." + r.getMemberByString()))).filter(c->c!=null).collect(Collectors.toSet());
    }

    public UUID joinGame(GameRequest request, UUID userId, String country) {
        log.info("user:"+userId+" attempting to join:"+request.getGameName()+" name space:"+characterLocation);


        try (Jedis jedis = jedisPool.getResource()){

            Optional<GameCharacter> available = getAllCharacters(jedis).stream()
                    .filter(c -> "SOLDIER".equals(c.props.get("type")) && "CADET_CORPORAL".equals(c.props.get("rank")) && country.equals(c.props.get("country")))
                    .findAny();

            if(!available.isPresent()){
                log.info("unable to find character to convert to avatar");
                return null;
            }
            log.info("converting:"+available.get().identity+" to avatar for user:"+userId+" on index:"+characterStatus);

            jedis.hset(characterStatus+"."+available.get().identity, "type", "AVATAR");
            jedis.hset(characterStatus+"."+available.get().identity, "userID", userId.toString());
            return UUID.fromString(available.get().identity);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    class GameCharacter{

        String identity;
        Map<String, String> props;

        public GameCharacter(String identity, Map<String, String> stringStringMap) {

            this.identity = identity;
            this.props = stringStringMap;
        }
    }


}
