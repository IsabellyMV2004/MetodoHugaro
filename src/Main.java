//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        int l, c, tracos;
        No [] matriz;
        No[] vetor;
        boolean min_tracos = false;
        carregarDadosArquivo(matriz,l,c);

        minLinha(matriz,l,c,vetor);
        subMinLinha(matriz,l,c,vetor);
        minColuna(matriz,l,c,vetor);
        subMinColuna(matriz,l,c,vetor);
        do {
            tracos = 0;
            minTracosZeros(matriz,l,c);
            contarTracos(matriz,tracos);
            if(tracos != l) {

                min_tracos = true;
            }
            else {

            }

        }while(!min_tracos);
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
        int pos;

        for (int i = 0; i < l; i++)
            vetL[i] = 0;

        for (int i = 0; i < c; i++)
            vetC[i] = 0;

        contarZeros(matriz,l,c,vetL,vetC);
        while (!vazio(vetL) && !vazio(vetC))
        {
            pos = buscarPos(vetL,2);
            vetL[pos]--;

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

    private static void contarTracos(No[] matriz, int tracos) {
    }
}