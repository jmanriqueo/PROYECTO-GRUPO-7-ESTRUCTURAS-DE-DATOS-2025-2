import java.util.*;

public class Matter {
    private int id;
    private String name;
    private List<List<Integer>> prerequisites; // Lista de listas para manejar opciones (OR)
    
    public Matter(int id, String name) {
        this.id = id;
        this.name = name;
        this.prerequisites = new ArrayList<>();
    }
    
    public Matter(int id, String name, List<List<Integer>> prerequisites) {
        this.id = id;
        this.name = name;
        this.prerequisites = prerequisites;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public List<List<Integer>> getPrerequisites() {
        return prerequisites;
    }
    
    // Setters
    public void setPrerequisites(List<List<Integer>> prerequisites) {
        this.prerequisites = prerequisites;
    }
    
    public void addPrerequisite(List<Integer> prerequisiteGroup) {
        this.prerequisites.add(prerequisiteGroup);
    }
    
    @Override
    public String toString() {
        return "Matter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prerequisites=" + prerequisites +
                '}';
    }
}



