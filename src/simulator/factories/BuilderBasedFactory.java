package simulator.factories;

import org.json.JSONObject;
import java.util.*;

public class BuilderBasedFactory<T> implements Factory<T> {
    private Map<String, Builder<T>> _builders;
    private List<JSONObject> _buildersInfo;

    public BuilderBasedFactory() {
        _builders = new HashMap<>();
        _buildersInfo = new LinkedList<>();
    }

    public BuilderBasedFactory(List<Builder<T>> builders) {
        this();
        for (Builder<T> builder : builders) {
            add_builder(builder);
        }
    }

    public void add_builder(Builder<T> b) {
        _builders.put(b.get_type_tag(), b);
        _buildersInfo.add(b.get_info());
    }

    @Override
    public T create_instance(JSONObject info) {
        if (info == null)
            throw new IllegalArgumentException("'info' cannot be null");

        String typeTag = info.getString("type");
        Builder<T> builder = _builders.get(typeTag);

        if (builder != null) {
            JSONObject data = info.optJSONObject("data");
            /*for (String key : data.keySet()) {
            	System.out.println(key + "  " + data.getString(key));
            }*/
            return builder.create_instance(data != null ? data : new JSONObject());
        } else {
            throw new IllegalArgumentException("Unrecognized 'info': " + info.toString());
        }
    }

    @Override
    public List<JSONObject> get_info() {
        return Collections.unmodifiableList(_buildersInfo);
    }
}
