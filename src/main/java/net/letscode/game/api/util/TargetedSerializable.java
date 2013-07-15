package net.letscode.game.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

/**
 * Provides functionality for targeted serialization. Specifically, targeted
 * serialization provides similar functionality to normal JSON serialization,
 * but also provides contextual information about the "target" of the serialized
 * data.
 * <p>A target can potentially be any class, but it should be able to provide
 * contextual information that can limit the scope of data actually serialized.
 * For example, a Zone may implement a
 * <code>serializeFor(JsonGenerator, Entity)</code> that only shows entities
 * within the view distance of the provided entity. The specific implementation
 * details are left to the implementing classes.</p>
 * @author timothyb89
 * @param <T> the target type to serialize for
 */
public interface TargetedSerializable<T> extends JsonSerializable {
	
	public void serializeFor(JsonGenerator g, T target) throws IOException;
	
}
