package io.github.zkhan93.familyfinance.util;

import io.github.zkhan93.familyfinance.models.BaseModel;

public interface ItemInteractionListener<T extends BaseModel> {
    void delete(T item);

    void edit(T item);

    void view(T item);

    void copyToClipboard(T item);
}
