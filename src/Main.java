import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static final String RESET = "\u001B[0m";
    public static String[] COLOR;
    public static String[] BG_COLOR;

    public static int linhasArq = 0;
    public static int colunasArq = 0;

    public static void main(String[] args) throws IOException {
        String caminho = new java.io.File("out\\production\\MetodoHugaro").getAbsolutePath() + "\\entrada.txt";
        String[][] matrizTxt = carregarDadosArq(caminho);
        String matrizEmString = matrizString(matrizTxt);
        inicializarCores();

        System.out.println("Linhas: " + linhasArq);
        System.out.println("Colunas: " + colunasArq);
        System.out.println("Matriz do arquivo:\n" + matrizEmString);

        int l = matrizTxt.length, c = matrizTxt[0].length, tracos, corMin;
        double min;
        No[][] matriz = new No[l][c];
        No[] vetor;
        boolean min_tracos = false;
        carregarDados(matriz, l, c, matrizTxt);

        vetor = new No[l];
        minLinha(matriz, l, c, vetor);
        exibirMatriz(matriz, l, c, vetor, "L");
        subMinLinha(matriz, l, c, vetor);
        exibirMatriz(matriz, l, c, null, "");
        vetor = new No[c];
        minColuna(matriz, l, c, vetor);
        exibirMatriz(matriz, l, c, vetor, "C");
        subMinColuna(matriz, l, c, vetor);
        exibirMatriz(matriz, l, c, null, "");

        int safety = 0; // evita loop infinito acidental
        do {
            minTracosZeros(matriz, l, c);
            exibirMatriz(matriz, l, c, null, "");
            tracos = contarTracos(matriz, l, c);
            if (tracos == l) {
                System.out.println("Condição de término atingida (tracos == l)");
                enquadrarZeros(matriz, l, c);
                exibirMatriz(matriz, l, c, null, "");
                min_tracos = true;
            } else {
                System.out.println("Ajustando valores (caso tracos != l)...");
                // obter menor e cor menor via retorno (corrigido para retornar)
                int[] res = minCor(matriz, l, c);
                min = res[0];
                corMin = res[1];

                for (int i = 0; i < l; i++) {
                    for (int j = 0; j < c; j++) {
                        if (matriz[i][j].getCor() == 0)
                            matriz[i][j].setValor(matriz[i][j].getValor() - min);
                        else if (matriz[i][j].getCor() == corMin)
                            matriz[i][j].setValor(matriz[i][j].getValor() + min);
                    }
                }
                pintar(matriz, l, c, 0);
                exibirMatriz(matriz, l, c, null, "");
            }

            safety++;
            if (safety > 1000) {
                System.err.println("Interrompendo execução — safety limit atingido (possível loop).");
                break;
            }
        } while (!min_tracos);
    }

    public static void inicializarCores() {
        COLOR = new String[8];
        COLOR[0] = "\u001B[0m";    // RESET
        COLOR[1] = "\u001B[33m"; //YELLOW
        COLOR[2] = "\u001B[36m"; //CYAN
        COLOR[3] = "\u001B[32m"; //GREEN
        COLOR[4] = "\u001B[34m"; //BLUE
        COLOR[5] = "\u001B[31m"; //RED
        COLOR[6] = "\u001B[35m"; //PURPLE
        COLOR[7] = "\u001B[37m"; //WHITE

        BG_COLOR = new String[8];
        BG_COLOR[0] = "\u001B[0m";    // RESET
        BG_COLOR[1] = "\u001B[43m"; //BG_YELLOW
        BG_COLOR[2] = "\u001B[46m"; //BG_CYAN
        BG_COLOR[3] = "\u001B[40m"; //BG_BLACK
        BG_COLOR[4] = "\u001B[42m"; //BG_GREEN
        BG_COLOR[5] = "\u001B[44m"; //BG_BLUE
        BG_COLOR[6] = "\u001B[41m"; //BG_RED
        BG_COLOR[7] = "\u001B[45m"; //BG_PURPLE
    }

    private static void exibirMatriz(No[][] matriz, int l, int c, No[] vetor, String lc) {
        if (vetor != null && "L".equals(lc)) {
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < c; j++) {
                    System.out.print(BG_COLOR[Math.max(0, Math.min(7, matriz[i][j].getCor()))] + matriz[i][j].getValor() + "  ");
                }
                System.out.print("\t\t" + vetor[i].getValor() + "\n");
                System.out.print(BG_COLOR[0]);
            }
        } else {
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < c; j++) {
                    System.out.print(BG_COLOR[Math.max(0, Math.min(7, matriz[i][j].getCor()))] + matriz[i][j].getValor() + "  ");
                }
                System.out.print(BG_COLOR[0] + "\n");
            }
            if ("C".equals(lc) && vetor != null) {
                System.out.println();
                for (int i = 0; i < vetor.length; i++) {
                    System.out.print("" + vetor[i].getValor() + "  ");
                }
                System.out.println();
            }
        }
        System.out.print(BG_COLOR[0] + "\n");
    }

    public static void enquadrarZeros(No[][] matriz, int l, int c) {
        pintar(matriz, l, c, 0);
        boolean marcou = true;
        while (marcou) {
            marcou = false;
            for (int i = 0; i < l; i++)
                for (int j = 0; j < c; j++)
                    if (matriz[i][j].getValor() == 0 && !buscarZeroLinha(matriz, i, j, 1) && !buscarZeroColuna(matriz, i, j, 1)) {
                        matriz[i][j].setCor(1);
                        marcou = true;
                    }
        }
    }

    private static boolean buscarZeroLinha(No[][] matriz, int x, int y, int cor) {
        for (int i = 0; i < matriz[x].length; i++) {
            if (matriz[x][i].getValor() == 0 && matriz[x][i].getCor() != cor) return true;
        }
        return false;
    }

    private static boolean buscarZeroColuna(No[][] matriz, int x, int y, int cor) {
        for (int i = 0; i < matriz.length; i++) {
            if (matriz[i][y].getValor() == 0 && matriz[i][y].getCor() != cor) return true;
        }
        return false;
    }

    private static boolean buscarZero(No[][] matriz, int l, int c, int x, int y) {
        for (int i = 0; i < l; i++)
            if (matriz[i][y].getValor() == 0) return true;

        for (int i = 0; i < c; i++)
            if (matriz[x][i].getValor() == 0) return true;

        return false;
    }

    public static void pintar(No[][] matriz, int l, int c, int cor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++)
                matriz[i][j].setCor(cor);
    }

    private static void carregarDados(No[][] matriz, int l, int c, String[][] matrizTxt) {
        for (int x = 0; x < l; x++)
            for (int y = 0; y < c; y++)
                matriz[x][y] = new No(Double.parseDouble(matrizTxt[x][y]), 0);
    }

    private static void minLinha(No[][] matriz, int l, int c, No[] vetor) {
        double menor;
        for (int i = 0; i < l; i++) {
            menor = Double.POSITIVE_INFINITY;
            for (int j = 0; j < c; j++)
                menor = Math.min(menor, matriz[i][j].getValor());
            vetor[i] = new No(menor, 0);
        }
    }

    private static void subMinLinha(No[][] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++) {
                double aux = matriz[i][j].getValor();
                matriz[i][j].setValor(aux - vetor[i].getValor());
            }
    }

    private static void minColuna(No[][] matriz, int l, int c, No[] vetor) {
        double menor;
        for (int i = 0; i < c; i++) {
            menor = Double.POSITIVE_INFINITY;
            for (int j = 0; j < l; j++)
                menor = Math.min(menor, matriz[j][i].getValor());
            vetor[i] = new No(menor, 0);
        }
    }

    private static void subMinColuna(No[][] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < c; i++)
            for (int j = 0; j < l; j++) {
                double aux = matriz[j][i].getValor();
                matriz[j][i].setValor(aux - vetor[i].getValor());
            }
    }

    private static void minTracosZeros(No[][] matriz, int l, int c) {
        int vetC[] = new int[c], vetL[] = new int[l];
        int pos, valor = 2;

        // inicializa
        for (int i = 0; i < l; i++) vetL[i] = 0;
        for (int i = 0; i < c; i++) vetC[i] = 0;

        contarZeros(matriz, l, c, vetL, vetC);

        // valor vai de maior contagem até 1 (mantive a ideia do seu valor=2, mas melhor usar dinamicamente)
        int maxCount = 0;
        for (int v : vetL) if (v > maxCount) maxCount = v;
        for (int v : vetC) if (v > maxCount) maxCount = v;

        valor = maxCount;
        while (!vazio(vetL) && !vazio(vetC) && valor > 0) {
            pos = buscarPos(vetC, valor);
            if (pos != -1) {
                vetC[pos] = 0;
                for (int i = 0; i < l; i++) {
                    // verificar limites e apenas incrementar cor se existir
                    matriz[i][pos].setCor(matriz[i][pos].getCor() + 1);
                    if (matriz[i][pos].getValor() == 0) vetL[i] = Math.max(0, vetL[i] - 1);
                }
            }

            pos = buscarPos(vetL, valor);
            if (pos != -1) {
                vetL[pos] = 0;
                for (int i = 0; i < c; i++) {
                    matriz[pos][i].setCor(matriz[pos][i].getCor() + 1);
                    if (matriz[pos][i].getValor() == 0) vetC[i] = Math.max(0, vetC[i] - 1);
                }
            }

            valor--;
        }
    }

    private static int buscarPos(int vet[], int valor) {
        for (int i = 0; i < vet.length; i++) {
            if (vet[i] == valor) return i;
        }
        return -1;
    }

    private static boolean vazio(int vet[]) {
        for (int i = 0; i < vet.length; i++) {
            if (vet[i] != 0) return false;
        }
        return true;
    }

    private static void contarZeros(No[][] matriz, int l, int c, int vetL[], int vetC[]) {
        for (int i = 0; i < l; i++) {
            vetL[i] = 0;
            for (int j = 0; j < c; j++) {
                if (Math.abs(matriz[i][j].getValor()) < 1e-9) vetL[i]++;
            }
        }

        for (int j = 0; j < c; j++) {
            vetC[j] = 0;
            for (int i = 0; i < l; i++) {
                if (Math.abs(matriz[i][j].getValor()) < 1e-9) vetC[j]++;
            }
        }
    }

    private static int contarTracos(No[][] matriz, int l, int c) {
        int cont, traco = 0;

        for (int i = 0; i < l; i++) {
            cont = 0;
            for (int j = 0; j < c; j++) {
                if (matriz[i][j].getCor() > 0) cont++;
            }
            if (cont == c) traco++;
        }

        for (int j = 0; j < c; j++) {
            cont = 0;
            for (int i = 0; i < l; i++) {
                if (matriz[i][j].getCor() > 0) cont++;
            }
            if (cont == l) traco++;
        }

        return traco;
    }

    // retorna [menor, corMenor] (double, int em double)
    private static int[] minCor(No[][] matriz, int l, int c) {
        double menor = Double.POSITIVE_INFINITY;
        int corMenor = -1;

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                if (matriz[i][j].getCor() > 1 && matriz[i][j].getValor() < menor) {
                    menor = matriz[i][j].getValor();
                    corMenor = matriz[i][j].getCor();
                }
            }
        }

        if (corMenor == -1) {
            // fallback: se não encontrou (nenhuma cor>1), pega menor global
            menor = Double.POSITIVE_INFINITY;
            for (int i = 0; i < l; i++)
                for (int j = 0; j < c; j++)
                    if (matriz[i][j].getValor() < menor) {
                        menor = matriz[i][j].getValor();
                        corMenor = matriz[i][j].getCor();
                    }
        }

        int menorInt;
        if (Double.isInfinite(menor)) menorInt = Integer.MAX_VALUE;
        else menorInt = (int) Math.round(menor);

        return new int[]{menorInt, corMenor};
    }

    public static String[][] carregarDadosArq(String caminho) throws IOException {
        List<String[]> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    linhas.add(linha.split("\\s+"));
                }
            }
        }
        if (linhas.isEmpty()) throw new IllegalArgumentException("Arquivo vazio ou inválido: " + caminho);
        linhasArq = linhas.size();
        colunasArq = linhas.get(0).length;
        return linhas.toArray(new String[0][]);
    }

    public static String matrizString(String[][] matriz) {
        StringBuilder builder = new StringBuilder();
        for (String[] linha : matriz) {
            for (String valor : linha) {
                builder.append(valor).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
