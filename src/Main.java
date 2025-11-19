import java.io.*;
import java.util.*;

public class Main {
    private static final double EPS = 1e-9;

    // ANSI background color para destacar zeros (pode não funcionar em todas IDEs/terminals)
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_RESET  = "\u001B[0m";

    public static void main(String[] args) throws Exception {
        double[][] input;

        // tenta carregar de arquivo entrada.txt no diretório de execução
        File f = new File("entrada.txt");
        if (f.exists()) {
            System.out.println("Procurando arquivo no diretório: " +
                    new File("").getAbsolutePath());

            input = carregarDadosArq("entrada.txt");

        } else {
            System.out.println("Arquivo entrada.txt não encontrado — usando matriz embutida (fornecida).");
            input = new double[][] {
                    {5, 24, 13, 7},
                    {10,25, 3, 23},
                    {28, 9,  8, 5},
                    {10,17,15, 3}
            };
        }

        // resolver
        HungarianResult res = hungarianWithSteps(input);

        // mostrar resultado
        System.out.println("\n=== Resultado Final ===");
        System.out.println("Assignment (linha -> coluna):");
        for (int i = 0; i < input.length; i++) {
            int j = res.assignment[i];
            if (j >= 0 && j < input[0].length) {
                System.out.printf("  linha %d -> coluna %d%n", i, j);
            } else {
                System.out.printf("  linha %d -> coluna %d   (padded ou inválido)%n", i, j);
            }
        }
        //System.out.printf("Custo total (somente linhas originais): %f%n", res.totalCost);
    }

    /** Estrutura para resultado */
    private static class HungarianResult {
        int[] assignment; // assignment[row] = col
        double totalCost;
    }

    /** Implementação do algoritmo com prints a cada passo importante */
    private static HungarianResult hungarianWithSteps(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        int n = Math.max(rows, cols); // dimensão quadrada após padding
        double INF = 1e9;

        // matrix 'a' será n x n, preenchida com INF para posições fictícias
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) Arrays.fill(a[i], INF);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                a[i][j] = input[i][j];

        System.out.println("\nMatriz padronizada (com padding até " + n + "x" + n + "):");
        imprimirMatriz(a, -1);

        // 1) redução por linha
        for (int i = 0; i < n; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) min = Math.min(min, a[i][j]);
            if (!Double.isInfinite(min) && Math.abs(min) > EPS)
                for (int j = 0; j < n; j++) a[i][j] -= min;
        }
        System.out.println("\nApós redução por linha:");
        imprimirMatriz(a, 0); // destaque zeros

        // 2) redução por coluna
        for (int j = 0; j < n; j++) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) min = Math.min(min, a[i][j]);
            if (!Double.isInfinite(min) && Math.abs(min) > EPS)
                for (int i = 0; i < n; i++) a[i][j] -= min;
        }
        System.out.println("\nApós redução por coluna:");
        imprimirMatriz(a, 0);

        // iterar até obter matching perfeito
        int[] matchCol = new int[n]; // matchCol[j] = i or -1
        boolean finished = false;
        int iteration = 0;
        while (!finished) {
            iteration++;
            Arrays.fill(matchCol, -1);

            // encontrar matching máximo em grafo de zeros (bipartite rows->cols)
            for (int r = 0; r < n; r++) {
                boolean[] seen = new boolean[n];
                tryKuhn(r, a, matchCol, seen);
            }
            int matched = 0;
            for (int j = 0; j < n; j++) if (matchCol[j] != -1) matched++;

            System.out.println("\nIteração " + iteration + " -> tamanho do matching: " + matched + " de " + n);
            // mostrar zeros destacados
            imprimirMatriz(a, 0);

            if (matched == n) {
                System.out.println("Encontrado matching perfeito.");
                finished = true;
                break;
            }

            // construir cobertura mínima (Kőnig): linhas NÃO visitadas + colunas visitadas (via alternate DFS)
            boolean[] rowVisited = new boolean[n];
            boolean[] colVisited = new boolean[n];
            boolean[] rowHasMatch = new boolean[n];
            for (int j = 0; j < n; j++) if (matchCol[j] != -1) rowHasMatch[matchCol[j]] = true;
            for (int i = 0; i < n; i++) {
                if (!rowHasMatch[i]) alternateDFS(i, a, matchCol, rowVisited, colVisited);
            }
            // cover: rows NOT visited, cols visited
            boolean[] coverRow = new boolean[n], coverCol = new boolean[n];
            int lines = 0;
            for (int i = 0; i < n; i++) {
                coverRow[i] = !rowVisited[i];
                if (coverRow[i]) lines++;
            }
            for (int j = 0; j < n; j++) {
                coverCol[j] = colVisited[j];
                if (coverCol[j]) lines++;
            }

            System.out.println("Linhas cobertas: " + Arrays.toString(coverRow));
            System.out.println("Colunas cobertas: " + Arrays.toString(coverCol));
            System.out.println("Número total de linhas usadas para cobrir zeros: " + lines);

            // achar menor elemento não coberto
            double minUncovered = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
                if (!coverRow[i] && !coverCol[j]) minUncovered = Math.min(minUncovered, a[i][j]);
            }

            if (Double.isInfinite(minUncovered)) {
                System.err.println("Nenhum elemento não coberto encontrado — abortando.");
                break;
            }

            System.out.println("Menor elemento não coberto = " + minUncovered);

            // ajustar: subtrair minUncovered de não cobertos; adicionar a double-covered
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
                if (!coverRow[i] && !coverCol[j]) a[i][j] -= minUncovered;
                else if (coverRow[i] && coverCol[j]) a[i][j] += minUncovered;
            }

            System.out.println("Após ajuste (subtrai minUncovered dos não-cobertos, soma aos doubly covered):");
            imprimirMatriz(a, 0);

            // volta para tentar matching novamente
        }

        // construir assignment row->col a partir de matchCol
        int[] assignment = new int[rows];
        Arrays.fill(assignment, -1);
        for (int j = 0; j < n; j++) {
            if (matchCol[j] != -1 && matchCol[j] < rows && j < cols) {
                assignment[matchCol[j]] = j;
            }
            // se match para padded column, assignment pode ficar -1 ou apontar para padded.
        }

        // calcular custo apenas nas linhas originais
        /*double totalCost = 0;
        for (int i = 0; i < rows; i++) {
            int j = assignment[i];
            if (j >= 0 && j < cols) totalCost += input[i][j];
            else totalCost += INF; // penalidade se foi atribuído a coluna fictícia
        }*/

        HungarianResult out = new HungarianResult();
        out.assignment = assignment;
        //out.totalCost = totalCost;
        return out;
    }

    // matching (Kuhn) considering edges where a[row][j] == 0 (within EPS).
    private static boolean tryKuhn(int row, double[][] a, int[] matchCol, boolean[] seen) {
        int n = a.length;
        for (int j = 0; j < n; j++) {
            if (!seen[j] && Math.abs(a[row][j]) <= EPS) {
                seen[j] = true;
                if (matchCol[j] == -1 || tryKuhn(matchCol[j], a, matchCol, seen)) {
                    matchCol[j] = row;
                    return true;
                }
            }
        }
        return false;
    }

    // DFS alternante para encontrar vértices visitáveis a partir de linha livre
    private static void alternateDFS(int row, double[][] a, int[] matchCol, boolean[] rowVisited, boolean[] colVisited) {
        rowVisited[row] = true;
        int n = a.length;
        for (int j = 0; j < n; j++) {
            if (Math.abs(a[row][j]) <= EPS && !colVisited[j]) {
                colVisited[j] = true;
                if (matchCol[j] != -1 && !rowVisited[matchCol[j]]) {
                    alternateDFS(matchCol[j], a, matchCol, rowVisited, colVisited);
                }
            }
        }
    }

    /** Imprime matriz; destaca zeros (valor <= EPS) pintando BG_YELLOW; se highlightZeroColOrRow >=0, destaca toda coluna/linha */
    private static void imprimirMatriz(double[][] a, int highlightZeroMode) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double v = a[i][j];
                boolean isZero = Math.abs(v) <= EPS;
                if (isZero) System.out.print(BG_YELLOW);
                if (Double.isInfinite(v)) System.out.printf("%8s ", "INF");
                else System.out.printf("%8.3f ", v);
                if (isZero) System.out.print(BG_RESET);
            }
            System.out.println();
        }
    }

    /** Lê uma matriz de um arquivo com números separados por espaço */
    public static double[][] carregarDadosArq(String caminho) throws IOException {
        List<double[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                double[] vals = new double[parts.length];
                for (int i = 0; i < parts.length; i++) vals[i] = Double.parseDouble(parts[i]);
                rows.add(vals);
            }
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("Arquivo vazio ou inválido: " + caminho);
        int cols = rows.get(0).length;
        for (double[] r : rows) if (r.length != cols) throw new IllegalArgumentException("Todas as linhas devem ter o mesmo número de colunas");
        double[][] m = new double[rows.size()][cols];
        for (int i = 0; i < rows.size(); i++) m[i] = rows.get(i);
        return m;
    }
}
