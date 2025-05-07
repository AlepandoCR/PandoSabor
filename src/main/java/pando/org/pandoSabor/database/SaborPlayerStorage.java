package pando.org.pandoSabor.database;

import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SaborPlayerStorage {

    private final Connection connection;

    private final PandoSabor plugin;

    public SaborPlayerStorage(Connection connection, PandoSabor plugin) {
        this.connection = connection;
        this.plugin = plugin;
    }

    public void save(SaborPlayer player) {
        try {
            plugin.getLogger().info("[DEBUG] Guardando datos del jugador: " + player.getUuid());

            Class<?> clazz = SaborPlayer.class;
            Field[] fields = clazz.getDeclaredFields();

            StringBuilder query = new StringBuilder("REPLACE INTO sabor_players (");
            StringBuilder values = new StringBuilder(" VALUES (");

            List<Object> paramValues = new ArrayList<>();

            for (Field field : fields) {
                field.setAccessible(true);
                query.append(field.getName()).append(",");
                values.append("?,");

                Object value = field.get(player);

                if (value instanceof UUID) {
                    value = value.toString();
                }

                if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    value = list.stream().map(Object::toString).collect(Collectors.joining(","));
                }

                paramValues.add(value);

            }

            // Quitar la última coma
            query.setLength(query.length() - 1);
            values.setLength(values.length() - 1);
            query.append(")").append(values).append(")");

            plugin.getLogger().info("[DEBUG] Query SQL generada: " + query);
            plugin.getLogger().info("[DEBUG] Valores: " + paramValues);

            try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
                for (int i = 0; i < paramValues.size(); i++) {
                    stmt.setObject(i + 1, paramValues.get(i));
                }
                stmt.executeUpdate();
                plugin.getLogger().info("[DEBUG] Datos guardados exitosamente para " + player.getUuid());
            }

        } catch (Exception e) {
            plugin.getLogger().info("[ERROR] Error al guardar datos del jugador:");
            e.printStackTrace();
        }
    }

    @NotNull
    public SaborPlayer load(UUID uuid) {
        try {
            plugin.getLogger().info("[DEBUG] Cargando datos del jugador: " + uuid);
            String sql = "SELECT * FROM sabor_players WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        SaborPlayer player = new SaborPlayer(uuid);
                        Field[] fields = SaborPlayer.class.getDeclaredFields();

                        for (Field field : fields) {
                            field.setAccessible(true);
                            String name = field.getName();

                            if (field.getType() == UUID.class) {
                                field.set(player, UUID.fromString(rs.getString(name)));
                            } else if (field.getType() == List.class) {
                                String raw = rs.getString(name);
                                List<UUID> list = new ArrayList<>();
                                if (raw != null && !raw.isEmpty()) {
                                    for (String s : raw.split(",")) {
                                        list.add(UUID.fromString(s));
                                    }
                                }
                                field.set(player, list);
                            } else {
                                field.set(player, rs.getObject(name));
                            }
                        }

                        plugin.getLogger().info("[DEBUG] Datos cargados correctamente para " + uuid);
                        return player;
                    } else {
                        plugin.getLogger().info("[DEBUG] No se encontraron datos para el jugador, creando " + uuid);
                        return new SaborPlayer(uuid);
                    }
                }
            }

        } catch (Exception e) {
            plugin.getLogger().info("[ERROR] Error al cargar datos del jugador:");
            e.printStackTrace();
        }
        plugin.getLogger().info("[DEBUG] No se encontraron datos para el jugador, creando " + uuid);
        return new SaborPlayer(uuid);
    }

    public void createTableIfNotExists() {
        try {
            plugin.getLogger().info("[DEBUG] Verificando/creando tabla 'sabor_players'...");

            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS sabor_players (");

            Field[] fields = SaborPlayer.class.getDeclaredFields();
            List<String> columns = new ArrayList<>();

            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String type = getSQLType(field.getType());
                columns.add(name + " " + type);
            }

            sb.append(String.join(", ", columns));
            sb.append(", PRIMARY KEY (uuid));");

            plugin.getLogger().info("[DEBUG] Query de creación de tabla: " + sb);

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sb.toString());
                plugin.getLogger().info("[DEBUG] Tabla 'sabor_players' verificada/creada correctamente.");
            }

        } catch (SQLException e) {
            plugin.getLogger().info("[ERROR] Error al crear/verificar la tabla:");
            e.printStackTrace();
        }
    }

    private String getSQLType(Class<?> type) {
        if (type == int.class || type == Integer.class) return "INT";
        if (type == long.class || type == Long.class) return "BIGINT";
        if (type == double.class || type == Double.class) return "DOUBLE";
        if (type == float.class || type == Float.class) return "FLOAT";
        if (type == boolean.class || type == Boolean.class) return "BOOLEAN";
        if (type == String.class || type == UUID.class) return "VARCHAR(255)";
        if (type == List.class) return "TEXT"; // serializamos como string
        return "TEXT";
    }

    public void syncTableStructure() {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            String tableName = "sabor_players";

            // Verificar si la tabla existe
            ResultSet tables = meta.getTables(null, null, tableName, null);
            if (!tables.next()) {
                // No existe, crearla completamente
                createTableIfNotExists();
                return;
            }

            // Tabla existe: obtener columnas existentes
            ResultSet columns = meta.getColumns(null, null, tableName, null);
            Set<String> existingColumns = new HashSet<>();
            Map<String, String> existingTypes = new HashMap<>();
            while (columns.next()) {
                String colName = columns.getString("COLUMN_NAME").toLowerCase();
                String type = columns.getString("TYPE_NAME").toUpperCase();
                existingColumns.add(colName);
                existingTypes.put(colName, type);
            }

            Class<?> clazz = SaborPlayer.class;
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                String fieldName = field.getName().toLowerCase();
                String sqlType = getSQLTypeForField(field);

                if (!existingColumns.contains(fieldName)) {
                    String alter = "ALTER TABLE " + tableName + " ADD COLUMN " + fieldName + " " + sqlType + ";";
                    plugin.getLogger().info("[DEBUG] Agregando nueva columna: " + fieldName + " (" + sqlType + ")");
                    try (Statement stmt = connection.createStatement()) {
                        stmt.executeUpdate(alter);
                    }
                } else if (fieldName.equals("uuid") && !existingTypes.get(fieldName).startsWith("VARCHAR")) {
                    String alter = "ALTER TABLE " + tableName + " MODIFY COLUMN uuid VARCHAR(36);";
                    plugin.getLogger().info("[DEBUG] Corrigiendo tipo de uuid a VARCHAR(36)");
                    try (Statement stmt = connection.createStatement()) {
                        stmt.executeUpdate(alter);
                    }
                }
            }

        } catch (SQLException e) {
            plugin.getLogger().info("[ERROR] Error al sincronizar estructura de tabla:");
            e.printStackTrace();
        }
    }

    private String getSQLTypeForField(Field field) {
        Class<?> type = field.getType();

        if (type == UUID.class) return "VARCHAR(36)";
        if (type == int.class || type == Integer.class) return "INT";
        if (type == List.class) return "TEXT"; // para listas serializadas
        if (type == String.class) return "TEXT";

        return "TEXT"; // Fallback
    }


}
