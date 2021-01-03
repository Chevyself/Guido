package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.NonNull;

/** A tab list entry with a skin */
public abstract class TabListSkinEntry implements TabListEntry {

  /**
   * Get the skin of the entry
   *
   * @return the skin of the entry
   */
  @NonNull
  abstract String getSkin();

  /**
   * Get the signature of the skin
   *
   * @return the signature of the skin
   */
  @NonNull
  abstract String getSignature();

  /**
   * Get the property of the skin
   *
   * @return the property of the skin
   */
  @NonNull
  public WrappedSignedProperty getProperty() {
    return new WrappedSignedProperty("textures", this.getSkin(), this.getSignature());
  }

  /**
   * Get the entry as a wrapped game profile
   *
   * @return the wrapped game profile of the entry
   */
  @Override
  public @NonNull WrappedGameProfile toWrappedGameProfile() {
    WrappedGameProfile profile = TabListEntry.super.toWrappedGameProfile();
    profile.getProperties().put("textures", this.getProperty());
    return profile;
  }
}
