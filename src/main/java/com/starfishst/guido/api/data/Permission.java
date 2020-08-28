package com.starfishst.guido.api.data;

import org.jetbrains.annotations.NotNull;

/**
 * This object represents a permission. A permission contains a node and
 * whether it is enabled
 */
public interface Permission {

    /**
     * Get the node of the permission. This node is the representation of
     * the permission as a string
     *
     * @return the node if the permission
     */
    @NotNull
    String getNode();

    /**
     * Get if the permission is enabled. If the permission is enabled it means that the member is allowed to use it
     *
     * @return whether the permission is enabled
     */
    boolean isEnabled();

}