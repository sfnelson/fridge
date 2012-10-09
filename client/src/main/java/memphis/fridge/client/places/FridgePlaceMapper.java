package memphis.fridge.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@WithTokenizers({LoginPlace.Tokenizer.class, PurchasePlace.Tokenizer.class})
public interface FridgePlaceMapper extends PlaceHistoryMapper {
}
