import java.io.*;
import java.util.*;

/**
 * Implementação simples e correta do Método Húngaro (versão clássica, sem cores).
 * Lê matriz de custos do arquivo resources/entrada.txt (cada linha: números separados por espaços).
 */
public class HungarianSimple {

    // Tolerância para comparar com zero (evita problemas de ponto flutuante)
    private static final double EPS = 1e-9;

    public static void main(String[] args) throws IOException {
        String caminho = new java.io.File("out\\production\\MetodoHugaro").getAbsolutePath()+"\\entrada.txt";
        double[][] input = carregarDadosArq(caminho);

        int n = Math.max(input.length, input[0].length);
        double INF = 1e6; // custo alto para posições fictícias
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++)
            Arrays.fill(a[i], INF);

        for (int i = 0; i < input.length; i++)
            for (int j = 0; j < input[0].length; j++)
                a[i][j] = input[i][j];

        System.out.println("Matriz inicial (padronizada a " + n + "x" + n + "):");
        imprimirMatriz(a);

        // redução por linha
        for (int i = 0; i < n; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++)
                min = Math.min(min, a[i][j]);
            if (!Double.isInfinite(min) && Math.abs(min) > EPS)
                for (int j = 0; j < n; j++)
                    a[i][j] -= min;
        }

        System.out.println("\nApós reduções por linha:");
        imprimirMatriz(a);

        // redução por coluna
        for (int j = 0; j < n; j++) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++)
                min = Math.min(min, a[i][j]);
            if (!Double.isInfinite(min) && Math.abs(min) > EPS)
                for (int i = 0; i < n; i++)
                    a[i][j] -= min;
        }

        System.out.println("\nApós reduções por coluna:");
        imprimirMatriz(a);

        // iteração principal
        int[] matchCol = new int[n]; // matchCol[j] = i (row), ou -1 se livre
        while (true) {
            // construir grafo de zeros e encontrar emparelhamento máximo
            Arrays.fill(matchCol, -1);
            for (int row = 0; row < n; row++) {
                boolean[] seen = new boolean[n];
                tryKuhn(row, a, matchCol, seen);
            }

            int matchingSize = 0;
            for (int j = 0; j < n; j++) if (matchCol[j] != -1) matchingSize++;

            if (matchingSize == n) {
                // Já temos uma atribuição completa
                System.out.println("\nEncontrada atribuição completa (matching size == n).");
                break;
            }

            // determinar cobertura mínima de linhas/colunas a partir do matching
            boolean[] rowVisited = new boolean[n];
            boolean[] colVisited = new boolean[n];

            // iniciar por todas as linhas livres (linhas que não estão em match)
            boolean[] rowHasMatch = new boolean[n];
            for (int j = 0; j < n; j++)
                if (matchCol[j] != -1) rowHasMatch[matchCol[j]] = true;

            for (int i = 0; i < n; i++) {
                if (!rowHasMatch[i]) {
                    alternateDFS(i, a, matchCol, rowVisited, colVisited);
                }
            }

            // Min vertex cover: rows NOT visited, cols visited
            boolean[] coverRow = new boolean[n];
            boolean[] coverCol = new boolean[n];
            for (int i = 0; i < n; i++) coverRow[i] = !rowVisited[i];
            for (int j = 0; j < n; j++) coverCol[j] = colVisited[j];

            int lines = 0;
            for (boolean b : coverRow) if (b) lines++;
            for (boolean b : coverCol) if (b) lines++;
            System.out.println("\nLinhas cobradas (rows): " + Arrays.toString(coverRow));
            System.out.println("Colunas cobradas (cols): " + Arrays.toString(coverCol));
            System.out.println("Número de traços/linhas usadas: " + lines + " (matchingSize=" + matchingSize + ")");

            // encontrar menor elemento não coberto
            double minUncovered = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!coverRow[i] && !coverCol[j]) {
                        minUncovered = Math.min(minUncovered, a[i][j]);
                    }
                }
            }
            if (Double.isInfinite(minUncovered)) {
                // Algo estranho — quebra loop
                System.err.println("Nenhum elemento descoberto encontrado (infinito). Saindo.");
                break;
            }

            // 6) Subtrair minUncovered de todos os não cobertos, somar a todos doubly covered
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!coverRow[i] && !coverCol[j]) {
                        a[i][j] -= minUncovered;
                    } else if (coverRow[i] && coverCol[j]) {
                        a[i][j] += minUncovered;
                    }
                }
            }

            System.out.println("\nApós ajuste (minUncovered = " + minUncovered + "):");
            imprimirMatriz(a);
        }

        // matchCol: coluna -> linha. Vamos construir assignment row->col
        int nRows = n;
        int[] assignment = new int[nRows]; // assignment[i] = j
        Arrays.fill(assignment, -1);
        for (int j = 0; j < n; j++) {
            if (matchCol[j] != -1) assignment[matchCol[j]] = j;
        }

        // Calcular custo total na matrix original de entrada (cuidar do padding)
        double totalCost = 0.0;
        System.out.println("\nAtribuições (linha -> coluna):");
        for (int i = 0; i < input.length; i++) { // mostrar somente linhas originais
            int j = assignment[i];
            if (j >= 0 && j < input[0].length) {
                System.out.println("  linha " + i + " -> coluna " + j + "  custo = " + input[i][j]);
                totalCost += input[i][j];
            } else {
                System.out.println("  linha " + i + " -> coluna " + j + " (padded / custo alto)");
                totalCost += INF;
            }
        }
        System.out.println("Custo total (somente linhas originais): " + totalCost);
    }

    // Tenta encontrar caminho aumentante para a linha 'row' em grafo onde a[i][j] == 0
    private static boolean tryKuhn(int row, double[][] a, int[] matchCol, boolean[] seen) {
        int n = a.length;
        for (int j = 0; j < n; j++) {
            if (Math.abs(a[row][j]) <= EPS && !seen[j]) { // aresta (row,j) existe se o custo é zero
                seen[j] = true;
                if (matchCol[j] == -1 || tryKuhn(matchCol[j], a, matchCol, seen)) {
                    matchCol[j] = row;
                    return true;
                }
            }
        }
        return false;
    }

    // Alternating DFS usado para encontrar vértices visitáveis para construir min vertex cover
    // Começa em uma linha livre (row), marca linhas e colunas visitadas seguindo arestas:
    //   - de linha para coluna: se existe aresta zero e coluna não visitada
    //   - de coluna para linha: se coluna está matched com alguma linha (matchCol[col]) e linha não visitada
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

    // Imprime a matriz com formatação simples
    private static void imprimirMatriz(double[][] a) {
        for (double[] linha : a) {
            for (double v : linha) {
                System.out.printf("%10.3f ", v);
            }
            System.out.println();
        }
    }

    // Carrega uma matriz num arquivo (linhas de números separados por espaço)
    public static double[][] carregarDadosArq(String caminho) throws IOException {
        List<double[]> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] partes = linha.split("\\s+");
                double[] vals = new double[partes.length];
                for (int i = 0; i < partes.length; i++) vals[i] = Double.parseDouble(partes[i]);
                linhas.add(vals);
            }
        }
        if (linhas.isEmpty()) throw new IllegalArgumentException("Arquivo vazio ou inválido: " + caminho);
        int cols = linhas.get(0).length;
        for (double[] r : linhas) {
            if (r.length != cols) throw new IllegalArgumentException("Todas as linhas devem ter o mesmo número de colunas");
        }
        double[][] m = new double[linhas.size()][cols];
        for (int i = 0; i < linhas.size(); i++) m[i] = linhas.get(i);
        return m;
    }
}
