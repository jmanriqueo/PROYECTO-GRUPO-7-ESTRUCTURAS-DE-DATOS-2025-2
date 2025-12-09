import java.util.*;

public class PlanificadorSemestres {

    private MatterGraph graph;

    public PlanificadorSemestres(MatterGraph graph) {
        this.graph = graph;
    }

    /*
     * Calcula y devuelve únicamente el número mínimo de semestres.
     */
    public int calcularSemestresMinimos(int maxPerSemester) {
        List<List<Integer>> plan = planificarSemestres(maxPerSemester);
        return plan.size();
    }

    /**
     * Planificador por niveles usando:
     * - Algoritmo de Kahn (con cola propia del proyecto)
     * - Manejo de prerequisitos AND/OR
     * - Límite de materias por semestre
     */
    public List<List<Integer>> planificarSemestres(int maxPerSemester) {

        Map<Integer, Matter> matters = graph.getMatters();
        int n = matters.size();

        // Mapa de materias -> número de prerequisitos AND resueltos
        Map<Integer, Integer> indegree = new HashMap<>();

        // Inicializamos indegree en 0
        for (int id : matters.keySet()) {
            indegree.put(id, 0);
        }

        // Calculamos indegree basado en la estructura AND/OR
        for (Matter m : matters.values()) {
            List<List<Integer>> prereq = m.getPrerequisites();
            if (prereq == null) continue;

            for (List<Integer> group : prereq) {
                if (group.size() == 1) {
                    // AND puro
                    int req = group.get(0);
                    if (matters.containsKey(req)) {
                        indegree.put(m.getId(), indegree.get(m.getId()) + 1);
                    }
                } else {
                    // OR → se representa como un solo requisito
                    indegree.put(m.getId(), indegree.get(m.getId()) + 1);
                }
            }
        }

        // Cola personalizada
        ArrayQueue<Integer> cola = new ArrayQueue<>(n + 5);

        // Encolamos todos los indegree 0
        for (int id : indegree.keySet()) {
            if (indegree.get(id) == 0) {
                cola.enqueue(id);
            }
        }

        List<List<Integer>> resultado = new ArrayList<>();
        Set<Integer> vistos = new HashSet<>();

        // Procesamiento por niveles (semestres)
        while (!cola.isEmpty()) {

            List<Integer> nivel = new ArrayList<>();

            // Extraemos hasta el máximo permitido
            for (int i = 0; i < maxPerSemester && !cola.isEmpty(); i++) {
                int cur = cola.dequeue();
                nivel.add(cur);
                vistos.add(cur);
            }

            resultado.add(nivel);

            // Reducimos indegree de los dependientes
            for (int aprobado : nivel) {

                for (Matter m : matters.values()) {
                    if (vistos.contains(m.getId())) continue;

                    if (esPrerequisitoDe(aprobado, m)) {
                        int nuevoValor = indegree.get(m.getId()) - 1;
                        indegree.put(m.getId(), nuevoValor);

                        if (nuevoValor == 0) {
                            cola.enqueue(m.getId());
                        }
                    }
                }
            }
        }

        // Si no procesamos todas, existe un ciclo
        if (vistos.size() != matters.size()) {
            throw new IllegalStateException(
                    "Error: el grafo contiene un ciclo o prerequisitos imposibles de satisfacer."
            );
        }

        return resultado;
    }

    /**
     * Devuelve true si "a" aparece en algún grupo de prerequisitos de "m".
     * Compatible con AND/OR.
     */
    private boolean esPrerequisitoDe(int a, Matter m) {
        List<List<Integer>> groups = m.getPrerequisites();
        if (groups == null) return false;

        for (List<Integer> g : groups) {
            if (g.contains(a)) return true;
        }
        return false;
    }

    /**
     * Imprime el plan por consola.
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