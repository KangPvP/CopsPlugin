package fr.kanpvp.copsplugin.utlis;

import org.bukkit.entity.Entity;

public class VectorCal {

    public boolean entityCanSee(Entity entity1, Entity entity2) {
        org.bukkit.util.Vector vector1 = getVectorBetweenEntities(entity1, entity2);
        org.bukkit.util.Vector vector2 = entity1.getLocation().getDirection().normalize();
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
