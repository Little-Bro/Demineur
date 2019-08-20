import java.awt.*;

public class cellule
{
    int x,y;
    int indexI, indexJ;
    int chiffre;
    int cote;
    boolean mine;
    boolean revelee;
    boolean drapeau;

    public cellule(int indexI, int indexJ)
    {
        this.indexI = indexI;
        this.indexJ = indexJ;
        x = 50+indexI*50;
        y = 50+indexJ*50;
        cote = 50;
        chiffre = 0;
        revelee = false;
        drapeau = false;
    }

    public void dessine(Graphics g)
    {
        g.setColor(Color.black);
        g.drawRect(x, y, cote, cote);
    }

    public void compte(cellule listeCellules[][])
    {
        if (!mine)
        {
            chiffre = 0;
            for (int i=-1;i<=1;i++)
            {
                for (int j=-1;j<=1;j++)
                {
                    if (indexI +i>-1 && indexI+i<8 && indexJ+j>-1 && indexJ+j<8)
                    {
                        cellule voisin = listeCellules[indexI+i][indexJ+j];
                        if (voisin.mine)
                            chiffre++;
                    }
                }          
            }
        }
        else
            chiffre = -1;
    } 

    public void reveleVoisin(cellule listeCellules[][])
    {
        for (int i=-1;i<=1;i++)
        {
            for (int j=-1;j<=1;j++)
            {
                if (indexI +i>-1 && indexI+i<8 && indexJ+j>-1 && indexJ+j<8)
                {
                    cellule voisin = listeCellules[indexI+i][indexJ+j];
                    voisin.revelee = true;
                }
            }   
        }
    }
}