package fr.kanpvp.copsplugin.utlis;

import org.bukkit.entity.Entity;

public class VectorCal {

    public org.bukkit.util.Vector getLookDirection(float yaw, float pitch) {
        // Convert the Yaw and Pitch from degrees to radians
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);

        // Calculate the X, Y and Z components of the direction vector using trigonometric functions
        double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double y = Math.sin(pitchRadians);
        double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

        return new org.bukkit.util.Vector(x,y,z);
    }

    public boolean entityCanSee(Entity entity1, Entity entity2) {
        org.bukkit.util.Vector vector1 = getVectorBetweenEntities(entity1, entity2);
        org.bukkit.util.Vector vector2 = getLookDirection(entity1.getLocation().getYaw(), entity2.getLocation().getPitch());
        double angle = angleBetweenVectors(vector1, vector2);
        return angle < 120/2;
    }

    public org.bukkit.util.Vector getVectorBetweenEntities(Entity entity1, Entity entity2) {
        org.bukkit.util.Vector entity1Location = entity1.getLocation().toVector();
        org.bukkit.util.Vector entity2Location = entity2.getLocation().toVector();
        return entity2Location.subtract(entity1Location);
    }

    public double angleBetweenVectors(org.bukkit.util.Vector v1, org.bukkit.util.Vector v2) {
        double dotProduct = v1.dot(v2);
        double magnitudesProduct = v1.length() * v2.length();
        double angleRadians = Math.acos(dotProduct / magnitudesProduct);
        return Math.toDegrees(angleRadians);
    }

}
