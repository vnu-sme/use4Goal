package org.vnu.sme.goal.istar.mm;

/**
 * iStar 2.0 Social Dependency:
 *   depender --(D)--> dependum --(D)--> dependee
 *
 * dependum is the id of an intentional element (goal/task/resource/quality)
 * that acts as the depended-upon thing.
 */
public record Dependency(String depender, String dependum, String dependee) {}
