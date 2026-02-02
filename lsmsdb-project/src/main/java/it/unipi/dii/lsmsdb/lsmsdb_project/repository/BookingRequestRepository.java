package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.BookingRequest;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Repository
public class BookingRequestRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public BookingRequestRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(BookingRequest request) {
        String key = "req_" + request.getId();

        // 1. Scrittura sul Master
        redisTemplate.opsForValue().set(key, request, Duration.ofMinutes(30));

        // 2. WAIT Protocol (CP - Strong Consistency)
        // SOLUZIONE LUA SCRIPT:
        // Usiamo uno script Lua per chiamare 'WAIT' direttamente sul server.
        // Questo evita conflitti con il metodo Java Object.wait() e problemi di parsing.
        Long replicasAcknowledged = redisTemplate.execute((RedisCallback<Long>) connection -> {
            String luaScript = "return redis.call('WAIT', 2, 1000)";

            return connection.eval(
                    luaScript.getBytes(StandardCharsets.UTF_8),
                    ReturnType.INTEGER, // Diciamo esplicitamente che ci aspettiamo un numero
                    0 // Nessuna chiave passata allo script
            );
        });

        // 3. Verifica Rigorosa
        // Nota: convertiamo Long in int per il confronto
        if (replicasAcknowledged == null || replicasAcknowledged < 2) {
            // ROLLBACK COMPENSATIVO
            redisTemplate.delete(key);

            throw new RuntimeException("CONSISTENCY ERROR: Redis WAIT failed. " +
                    "Expected 2 replicas, got " + replicasAcknowledged + ". " +
                    "Booking rejected for safety.");
        }

        System.out.println("DEBUG: Redis Consistency OK. Replicated to " + replicasAcknowledged + " nodes.");
    }

    public Optional<BookingRequest> findById(String requestId) {
        Object data = redisTemplate.opsForValue().get("req_" + requestId);
        return Optional.ofNullable((BookingRequest) data);
    }

    public void deleteById(String requestId) {
        redisTemplate.delete("req_" + requestId);
    }

    public void deleteAll() {
        try {
            redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        } catch (Exception e) {
            // Ignora
        }
    }
}