package com.starfishst.bukkit.dependencies.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.starfishst.bukkit.api.config.GuidoListenerSettings;
import com.starfishst.bukkit.api.events.GuidoListener;
import java.lang.reflect.InvocationTargetException;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class VelocityImprovement implements GuidoListener {

  private double multiplierHorizontal;
  private double multiplierVertical;
  private double multiplierSprint;
  private double multiplierWalk;
  private double multiplierVerticalAir;
  private double friction;

  public VelocityImprovement() {
    GuidoListenerSettings settings = this.getSettings();
    this.multiplierHorizontal = settings.getOr("multiplier-horizontal", Double.class, 0.9);
    this.multiplierVertical = settings.getOr("multiplier-vertical", Double.class, 0.3);
    this.multiplierVerticalAir = settings.getOr("multiplier-vertical-air", Double.class, 0.15);
    this.multiplierSprint = settings.getOr("multiplier-sprint", Double.class, 0.8);
    this.multiplierWalk = settings.getOr("multiplier-walk", Double.class, 0.5);
    this.friction = settings.getOr("multiplier-walk", Double.class, 2.0);
  }

  @EventHandler
  public void onPlayerVelocity(PlayerVelocityEvent event) {
    Player player = event.getPlayer();
    EntityDamageEvent lastDamage = player.getLastDamageCause();
    if (!(lastDamage instanceof EntityDamageByEntityEvent)) return;

    if (((EntityDamageByEntityEvent) lastDamage).getDamager() instanceof Player) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.isCancelled()) return;
    if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
    Player damager = (Player) event.getDamager();
    Player entity = (Player) event.getEntity();
    if (entity.getNoDamageTicks() > (damager.getMaximumNoDamageTicks() / 2D)) return;
    double multiplierMovement = damager.isSprinting() ? this.multiplierSprint : this.multiplierWalk;
    double multiplierKnockback =
        damager.getItemInHand() == null
            ? 0
            : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2;
    double multiplierAir =
        entity.isOnGround() ? this.multiplierVertical : this.multiplierVerticalAir;
    Vector knockback =
        entity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
    double velocityX =
        (knockback.getX() * multiplierMovement + multiplierKnockback) * this.multiplierHorizontal;
    double velocityZ =
        (knockback.getZ() * multiplierMovement + multiplierKnockback) * this.multiplierHorizontal;
    knockback.setX(1 / this.friction * entity.getVelocity().getX() + velocityX);
    knockback.setY(1 / this.friction * entity.getVelocity().getY() + multiplierAir);
    knockback.setZ(1 / this.friction * entity.getVelocity().getZ() + velocityZ);
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
    packet.getModifier().writeDefaults();
    packet.getIntegers().write(0, entity.getEntityId());
    packet.getIntegers().write(1, (int) (knockback.getX() * 8000D));
    packet.getIntegers().write(2, (int) (knockback.getY() * 8000D));
    packet.getIntegers().write(3, (int) (knockback.getZ() * 8000D));
    try {
      manager.sendServerPacket(entity, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public void setFriction(double friction) {
    this.friction = friction;
  }

  public void setMultiplierHorizontal(double multiplierHorizontal) {
    this.multiplierHorizontal = multiplierHorizontal;
  }

  public void setMultiplierVertical(double multiplierVertical) {
    this.multiplierVertical = multiplierVertical;
  }

  public void setMultiplierSprint(double multiplierSprint) {
    this.multiplierSprint = multiplierSprint;
  }

  public void setMultiplierWalk(double multiplierWalk) {
    this.multiplierWalk = multiplierWalk;
  }

  public void setMultiplierVerticalAir(double multiplierVerticalAir) {
    this.multiplierVerticalAir = multiplierVerticalAir;
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "better-velocity";
  }
}
