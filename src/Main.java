import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        String[][] matrizTxt = carregarDadosArq("entrada.txt");
        String matrizEmString = matrizString(matrizTxt);

        System.out.println("Linhas: " + linhasArq);
        System.out.println("Colunas: " + colunasArq);
        System.out.println("Matriz do arquivo:\n" + matrizEmString);

        int l, c, tracos, min, corMin;
        No [] matriz;
        No[] vetor;
        boolean min_tracos = false;
        carregarDadosArquivo(matriz,l,c);

        minLinha(matriz,l,c,vetor);
        subMinLinha(matriz,l,c,vetor);
        minColuna(matriz,l,c,vetor);
        subMinColuna(matriz,l,c,vetor);
        do {
            minTracosZeros(matriz,l,c);
            tracos = contarTracos(matriz,l,c);
            if(tracos != l) {
                pintar(matriz,l,c,0);
                for (int i = 0; i < l; i++)
                    for (int j = 0; j < c; j++)
                        if(matriz[i][j].getValor() == 0 && !buscarZero(matriz,l,c,i,j))
                            matriz[i][j].setCor(1);




                min_tracos = true;
            }
            else {
                min = 999;
                corMin = 1;
                minCor(matriz,l,c,min,corMin);
                for (int i = 0; i < l; i++) {
                    for (int j = 0; j < c; j++) {
                        if(matriz[i][j].getCor() == 0)
                            matriz[i][j].setValor(matriz[i][j].getValor-min);
                        else
                        if(matriz[i][j].getCor() == corMin)
                            matriz[i][j].setValor(matriz[i][j].getValor+min);
                    }
                }
                pintar(matriz,l,c,0);
            }

        }while(!min_tracos);
    }

    public static void pintar(No[] matriz, int l, int c, int cor){
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++)
                matriz[i][j].setColor(cor);
    }

    private static void carregarDadosArquivo(No[] matriz, int l, int c) {
    }

    private static void minLinha(No[] matriz,int l, int c, No[] vetor) {
        int menor;
        for (int i = 0; i < l; i++) {
            menor = 9999;
            for (int j = 0; j < c; j++)
                if(matriz[i][j] < menor)
                    menor = matriz[i][j];
            vetor[i] = menor;
        }
    }

    private static void subMinLinha(No[] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++) {
                int aux = matriz[i][j].getValor();
                matriz[i][j].setValor(aux-vetor[i].getValor());
            }
    }

    private static void minColuna(No[] matriz, int l, int c, No[] vetor) {
        int menor;
        for (int i = 0; i < c; i++) {
            menor = 9999;
            for (int j = 0; j < l; j++)
                if(matriz[j][i] < menor)
                    menor = matriz[j][i];
            vetor[i] = menor;
        }
    }

    private static void subMinColuna(No[] matriz, int l, int c, No[] vetor) {
        for (int i = 0; i < c; i++)
            for (int j = 0; j < l; j++) {
                int aux = matriz[j][i].getValor();
                matriz[j][i].setValor(aux-vetor[j].getValor());
            }
    }

    private static void minTracosZeros(No[] matriz, int l, int c) {
        int vetC[] = new int[c], vetL[] = new int[l];
        int pos, int valor = 2;

        for (int i = 0; i < l; i++)
            vetL[i] = 0;

        for (int i = 0; i < c; i++)
            vetC[i] = 0;

        contarZeros(matriz,l,c,vetL,vetC);
        while (!vazio(vetL) && !vazio(vetC) && valor > 0)
        {
            pos = buscarPos(vetC,valor);
            vetC[pos]--;
            for (int i = 0; i < l; i++) {
                matriz[i][pos].setCor(matriz[i][pos].getColor()+1);
                vetL[i]--;
            }

            pos = buscarPos(vetL,valor);
            vetL[pos]--;
            for (int i = 0; i < c; i++) {
                matriz[pos][c].setCor(matriz[pos][i].getColor()+1);
                vetC[i]--;
            }

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

    private static void contarZeros(No[] matriz, int l, int c, int vetL[], int vetC[]){
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

    private static int contarTracos(No[] matriz, int l, int c) {
        int cont, traco = 0;

        for (int i = 0; i < l; i++) {
            cont = 0;
            for (int j = 0; j < c; j++) {
                if(matriz[i][j].getColor() > 0)
                    cont++;
            }
            if(cont == c)
                traco++;
        }

        for (int i = 0; i < c; i++) {
            cont = 0;
            for (int j = 0; j < l; j++) {
                if(matriz[j][i].getColor() > 0)
                    cont++;
            }
            if(cont == l)
                traco++;
        }

        return traco;
    }

    private static void minCor(No[] matriz, int l, int c, int menor, int corMenor) {
        for (int i = 0; i < l; i++)
            for (int j = 0; j < c; j++)
                if(matriz[i][j].getCor() > 1 && matriz[i][j].getValor() < menor)
                {
                    menor = matriz[i][j].getValor();
                    corMenor = matriz[i][j].getCor();
                }
    }

    private static boolean buscarZero(No[] matriz, int l, int c, int x, int y) {
        boolean flag = false;
        for (int i = 0; i < l; i++)
            if(matriz[i][y] == 0)
                flag = true;

        for (int i = 0; i < c; i++)
            if(matriz[x][i] == 0)
                flag = true;
        return flag;
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