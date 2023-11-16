package com.apeng.cpex.ex4;
import com.apeng.cpex.util.Pair;

import java.util.Objects;
import java.util.Set;


public class Project {
    private final Pair<String, String> formula;
    private final int dotPosition;
    public Project(String left, String rights, int dotPosition) {
        if (dotPosition > rights.length()) throw new RuntimeException("Dot position of project is out of bound.");
        formula = new Pair<>(left, rights);
        this.dotPosition = dotPosition;
    }

    public boolean hasNonSymbolAfterDot(Set<String> nonSymbolSet) {
        if (!hasNextPosition()) return false;
        return nonSymbolSet.contains(String.valueOf(formula.second().charAt(dotPosition)));
    }
    public String getNextSymbol() {
        return String.valueOf(getRights().charAt(dotPosition));
    }

    public String getLeft() {
        return formula.first();
    }
    public String getRights() {
        return formula.second();
    }
    public int getDotPosition() {
        return dotPosition;
    }
    public boolean hasNextPosition() {
        return dotPosition < formula.second().length();
    }
    public boolean hasNextSymbol() {
        return dotPosition < formula.second().length();
    }
    public Project ofNextPosition() {
        return new Project(this.getLeft(), this.getRights(), this.dotPosition + 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getLeft()).append("->");
        for (int i = 0; i < getRights().length(); i++) {
            if (i == dotPosition) builder.append("•");
            builder.append(getRights().charAt(i));
        }
        return builder.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return dotPosition == project.dotPosition && Objects.equals(formula, project.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formula, dotPosition);
    }
}
