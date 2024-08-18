package org.rhm;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public class Chat {
    String text;

    public Chat(Tag<?> tag) {
        if (tag instanceof CompoundTag) {
            text = tag.toString();
        } else {
            text = tag.toString();
        }
    }

    public String getText() {
        return text;
    }
}
