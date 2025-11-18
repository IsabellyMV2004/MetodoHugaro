import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String RESET = "\u001B[0m";
    public static String[] COLOR;
    public static String[] BG_COLOR;


    public static void main(String[] args) throws IOException {
        String[][] matrizTxt = carregarDadosArq(new java.io.File("out\\production\\MetodoHugaro").getAbsolutePath()+"\\entrada.txt");
        String matrizEmString = matrizString(matrizTxt);
        inicializarCores();



        System.out.println("Linhas: " + linhasArq);
        System.out.println("Colunas: " + colunasArq);
        System.out.println("Matriz do arquivo:\n" + matrizEmString);

        int l=matrizTxt.length, c=matrizTxt[0].length, tracos, corMin;
        double min;
        No [][] matriz = new No[l][c];
        No[] vetor;
        boolean min_tracos = false;
        carregarDados(matriz,l,c,matrizTxt);

        vetor = new No[l];
        minLinha(matriz,l,c,vetor);
        exibirMatriz(matriz,l,c,vetor,"L");
        subMinLinha(matriz,l,c,vetor);
        exibirMatriz(matriz,l,c,null,"");
        vetor = new No[c];
        minColuna(matriz,l,c,vetor);
        exibirMatriz(matriz,l,c,vetor,"C");
        subMinColuna(matriz,l,c,vetor);
        exibirMatriz(matriz,l,c,null,"");
        do {
            minTracosZeros(matriz,l,c);
            exibirMatriz(matriz,l,c,null,"");
            tracos = contarTracos(matriz,l,c);
            if(tracos == l) {
                System.out.println("AAAAAAAAAAAAAAAAA");
                enquadrarZeros(matriz,l,c);
                exibirMatriz(matriz,l,c,null,"");
                min_tracos = true;
            }
            else {
                System.out.println("BBBBBBBBBBBBBBBBBBB");
                min = 999;
                corMin = 0;
                exibirMatriz(matriz,l,c,null,"");
                minCor(matriz,l,c,min,corMin);
                for (int i = 0; i < l; i++) {
                    for (int j = 0; j < c; j++) {
                        if(matriz[i][j].getCor() == 0)
                            matriz[i][j].setValor(matriz[i][j].getValor()-min);
                        else
                        if(matriz[i][j].getCor() == corMin)
                            matriz[i][j].setValor(matriz[i][j].getValor()+min);
                    }
                }
                pintar(matriz,l,c,0);
                exibirMatriz(matriz,l,c,null,"");
            }

        }while(!min_tracos);
    }

    public static void inicializarCores(){

        COLOR = new String[8];
        COLOR[0] = "\u001B[0m";    // RESET
        COLOR[1] = "\u001B[33m"; //YELLOW
        COLOR[2] = "\u001B[36m"; //CYAN
      //  COLOR = "\u001B[30m"; //BLACK
        COLOR[3] = "\u001B[32m"; //GREEN
        COLOR[4] = "\u001B[34m"; //BLUE
        COLOR[5] = "\u001B[31m"; //RED
        COLOR[6] = "\u001B[35m"; //PURPLE
        COLOR[7] = "\u001B[37m"; //WHITE

        BG_COLOR = new String[8];
        BG_COLOR[0]  = "\u001B[0m";    // RESET
        BG_COLOR[1]  = "\u001B[43m"; //BG_YELLOW
        BG_COLOR[2]  = "\u001B[46m"; //BG_CYAN
        BG_COLOR[3]  = "\u001B[40m"; //BG_BLACK
        BG_COLOR[4]  = "\u001B[42m"; //BG_GREEN
        BG_COLOR[5] = "\u001B[44m"; //BG_BLUE
        BG_COLOR[6]  = "\u001B[41m"; //BG_RED
        BG_COLOR[7]  = "\u001B[45m"; //BG_PURPLE
     //   BG_COLOR  = "\u001B[47m"; //BG_WHITE
    }

    private static void exibirMatriz(No[][] matriz, int l, int c, No[] vetor, String lc) {
        if(vetor != null && lc.equals("L")){
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < c; j++) {
                    System.out.printf(BG_COLOR[matriz[i][j].getCor()]+"" + matriz[i][j].getValor()+"  ");
                }
                System.out.printf("\t\t" + vetor[i].getValor()+"\n");
                System.out.printf(BG_COLOR[0]);
            }
        }
        else{
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < c; j++) {
                    System.out.printf(BG_COLOR[matriz[i][j].getCor()]+"" + matriz[i][j].getValor()+"  ");
                }
                System.out.printf(BG_COLOR[0]+"\n");
            }
            if(lc.equals("C")) {
                System.out.println();
                for (int i = 0; i < vetor.length; i++) {
                    System.out.printf("" + vetor[i].getValor() + "  ");
                }
            }
        }

        System.out.printf(BG_COLOR[0]+"\n");
    }

    public static void enquadrarZeros(No[][] matriz, int l, int c){
        pintar(matriz,l,c,0);
        boolean marcou = true;
        while (marcou) {
            marcou = false;
            for (int i = 0; i < l; i++)
                for (int j = 0; j < c; j++)
                    if (matriz[i][j].getValor() == 0 && !buscarZeroLinha(matriz, l, c, i, j, 1) && !buscarZeroColuna(matriz, l, c, i, j, 1))
                    {
                        matriz[i][j].setCor(1);
                        marcou = true;
                    }
        }
    }

    private static boolean buscarZeroLinha(No[][] matriz, int l, int c, int x, int y, int cor) {
        boolean flag = false;
        for (int i = 0; i < l; i++)
            if(matriz[i][y].getValor() == 0 && matriz[i][y].getCor()!=cor)
                flag = true;
        return flag;
    }

    private static boolean buscarZeroColuna(No[][] matriz, int l, int c, int x, int y, int cor) {
        boolean flag = false;
        for (int i = 0; i < c; i++)
            if(matriz[x][i].getValor() == 0 && matriz[i][y].getCor()!=cor)
                flag = true;
        return flag;
    }

    private static boolean buscarZero(No[][] matriz, int l, int c, int x, int y) {
        boolean flag = false;
        for (int i = 0; i < l; i++)
            if(matriz[i][y].getValor() == 0)
                flag = true;

        for (int i = 0; i < c; i++)
            if(matriz[x][i].getValor() == 0)
                flag = true;
        return flag;
    }

    public static void pintar(No[][] matriz, int l, int c, int cor){
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++)
                matriz[i][j].setCor(cor);
    }

    private static void carregarDados(No[][] matriz, int l, int c, String[][] matrizTxt) {
        for(int x = 0; x < l; x++)
            for(int y = 0; y < c; y++)
                matriz[x][y] = new No(Double.parseDouble(matrizTxt[x][y]),0);
    }

    private static void minLinha(No[][] matriz,int l, int c, No[] vetor) {
        double menor;
        for (int i = 0; i < l; i++) {
            menor = 9999;
            for (int j = 0; j < c; j++)
                if(matriz[i][j].getValor() < menor)
                    menor = matriz[i][j].getValor();
            vetor[i] = new No(menor,0);
        }
    }

    private static void subMinLinha(No[][] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++) {
                double aux = matriz[i][j].getValor();
                matriz[i][j].setValor(aux-vetor[i].getValor());
            }
    }

    private static void minColuna(No[][] matriz, int l, int c, No[] vetor) {
        double menor;
        for (int i = 0; i < c; i++) {
            menor = 9999;
            for (int j = 0; j < l; j++)
                if(matriz[j][i].getValor() < menor)
                    menor = matriz[j][i].getValor();
            vetor[i] = new No(menor,0);
        }
    }

    private static void subMinColuna(No[][] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < c; i++)
            for (int j = 0; j < l; j++) {
                double aux = matriz[j][i].getValor();
                matriz[j][i].setValor(aux-vetor[i].getValor());
            }
    }

    private static void minTracosZeros(No[][] matriz, int l, int c) {
        int vetC[] = new int[c], vetL[] = new int[l], i;
        int pos, valor = 2;

        for (i = 0; i < l; i++)
            vetL[i] = 0;

        for (i = 0; i < c; i++)
            vetC[i] = 0;

        contarZeros(matriz,l,c,vetL,vetC);
        while (!vazio(vetL) && !vazio(vetC) && valor > 0)
        {
            pos = buscarPos(vetC,valor);
            vetC[pos]=0;
            for (i = 0; i < l; i++) {
                matriz[i][pos].setCor(matriz[i][pos].getCor()+1);
                if(matriz[i][pos].getValor() == 0)
                    vetL[i]--;
            }
            exibirMatriz(matriz,l,c,null,"");
            pos = buscarPos(vetL,valor);
            vetL[pos]=0;
            for (i = 0; i < c; i++) {
                matriz[pos][c].setCor(matriz[pos][i].getCor()+1);
                if(matriz[pos][i].getValor() == 0)
                    vetC[i]--;
                exibirMatriz(matriz,l,c,null,"");
            }
            exibirMatriz(matriz,l,c,null,"");
            valor--;
        }
    }

    private static int buscarPos(int vet[], int valor)
    {
        int i = 0;
        while (vet[i] != valor)
            i++;
        return i;
    }

    private static boolean vazio(int vet[])
    {
        boolean flag = false;
        int i = 0;
        while (i < vet.length && !flag)
            if (vet[i]!=0)
                flag = true;
        return flag;
    }

    private static void contarZeros(No[][] matriz, int l, int c, int vetL[], int vetC[]){
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                if(matriz[i][j].getValor() == 0)
                    vetL[i]++;
            }
        }

        for (int i = 0; i < c; i++) {
            for (int j = 0; j < l; j++) {
                if(matriz[j][i].getValor() == 0)
                    vetC[i]++;
            }
        }
    }

    private static int contarTracos(No[][] matriz, int l, int c) {
        int cont, traco = 0;

        for (int i = 0; i < l; i++) {
            cont = 0;
            for (int j = 0; j < c; j++) {
                if(matriz[i][j].getCor() > 0)
                    cont++;
            }
            if(cont == c)
                traco++;
        }

        for (int i = 0; i < c; i++) {
            cont = 0;
            for (int j = 0; j < l; j++) {
                if(matriz[j][i].getCor() > 0)
                    cont++;
            }
            if(cont == l)
                traco++;
        }

        return traco;
    }

    private static void minCor(No[][] matriz, int l, int c, double menor, int corMenor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++)
                if(matriz[i][j].getCor() > 1 && matriz[i][j].getValor() < menor)
                {
                    menor = matriz[i][j].getValor();
                    corMenor = matriz[i][j].getCor();
                }
    }


    public static int linhasArq = 0;
    public static int colunasArq = 0;

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
        linhasArq = linhas.size();
        colunasArq = linhas.get(0).length;

        return linhas.toArray(new String[0][]);
    }

    public static String matrizString(String[][] matriz){
        StringBuilder builder = new StringBuilder();
        for(String[] linha : matriz){
            for(String valor : linha){
                builder.append(valor).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
