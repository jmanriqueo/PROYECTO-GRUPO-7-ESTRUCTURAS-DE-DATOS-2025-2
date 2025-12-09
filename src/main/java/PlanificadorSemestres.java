import java.util.*;

public class PlanificadorSemestres {

    private MatterGraph graph;

    public PlanificadorSemestres(MatterGraph graph) {
        this.graph = graph;
    }

    /**
     * Calcula el número mínimo de semestres.
     */
    public int calcularSemestresMinimos(int maxPerSemester) {
        return planificarSemestres(maxPerSemester).size();
    }

    /**
     * Planificador por niveles usando:
     * - Algoritmo de Kahn
     * - SOLO prerequisitos AND
     * - Límite de materias por semestre
     */
    public List<List<Integer>> planificarSemestres(int maxPerSemester) {

        Map<Integer, Matter> matters = graph.getMatters();

        // indegree = número de prerequisitos pendientes
        Map<Integer, Integer> indegree = new HashMap<>();

        for (int id : matters.keySet()) {
            indegree.put(id, 0);
        }

        // Construcción correcta del indegree (SOLO AND)
        for (Matter m : matters.values()) {
            List<List<Integer>> prereqs = m.getPrerequisites();
            if (prereqs == null) continue;

            for (List<Integer> grupo : prereqs) {
                if (grupo.size() != 1) {
                    throw new IllegalStateException(
                        "Error de modelo: existen prerequisitos OR. Elimine optativas del grafo."
                    );
                }
                indegree.put(m.getId(), indegree.get(m.getId()) + 1);
            }
        }

        // Cola Kahn
        ArrayQueue<Integer> cola = new ArrayQueue<>(matters.size() + 5);

        for (int id : indegree.keySet()) {
            if (indegree.get(id) == 0) {
                cola.enqueue(id);
            }
        }

        List<List<Integer>> resultado = new ArrayList<>();
        Set<Integer> cursadas = new HashSet<>();

        // Procesamiento por semestres
        while (!cola.isEmpty()) {

            List<Integer> semestre = new ArrayList<>();

            while (semestre.size() < maxPerSemester && !cola.isEmpty()) {
                int cur = cola.dequeue();
                semestre.add(cur);
                cursadas.add(cur);
            }

            resultado.add(semestre);

            // Reducir indegree de dependientes
            for (int aprobada : semestre) {

                for (Matter m : matters.values()) {

                    if (cursadas.contains(m.getId())) continue;

                    List<List<Integer>> prereqs = m.getPrerequisites();
                    if (prereqs == null) continue;

                    for (List<Integer> grupo : prereqs) {
                        if (grupo.get(0) == aprobada) {
                            int nuevo = indegree.get(m.getId()) - 1;
                            indegree.put(m.getId(), nuevo);

                            if (nuevo == 0) {
                                cola.enqueue(m.getId());
                            }
                        }
                    }
                }
            }
        }

        // ✅ Validación FINAL (esta sí es la correcta)
        if (cursadas.size() != matters.size()) {
            throw new IllegalStateException(
                "Error: el grafo contiene ciclos o prerequisitos imposibles."
            );
        }

        return resultado;
    }

    /**
     * Imprime el plan.
     */
    public void imprimirPlan(List<List<Integer>> plan) {
        System.out.println("=== PLAN DE SEMESTRES ===");
        for (int i = 0; i < plan.size(); i++) {
            System.out.print("Semestre " + (i + 1) + ": ");
            for (int id : plan.get(i)) {
                Matter m = graph.getMatter(id);
                System.out.print(id + " (" + (m != null ? m.getName() : "?") + ")  ");
            }
            System.out.println();
        }
    }
}
