import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.Timer;
import java.util.*;

public class fenetreJeu extends JFrame implements ActionListener, MouseListener
{
    //ATTRIBUTS
    cellule[][] listeCellules;
    Timer chrono;
    Timer tempsPartie;
    int secondes, minutes;
    int mSecondes, mMinutes; 
    int nbreMines, nbreDrapeaux;
    int points;
    int randomI, randomJ;
    int optionJoueur;
    boolean gameOver;

    //images
    Image  r= Toolkit.getDefaultToolkit().getImage("revelee.png");
    Image pR = Toolkit.getDefaultToolkit().getImage("pasRevelee.png");
    Image dr = Toolkit.getDefaultToolkit().getImage("drapeau.png");
    Image m = Toolkit.getDefaultToolkit().getImage("mine.png");
    Image i1= Toolkit.getDefaultToolkit().getImage("1.png");
    Image i2= Toolkit.getDefaultToolkit().getImage("2.png");
    Image i3= Toolkit.getDefaultToolkit().getImage("3.png");
    
    //CONSTRUCTEUR
    public fenetreJeu(int nbreCase, int mMin, int mSec)
    {
        //paramètres de la fenêtre
        setTitle("Démineur");
        setLocation(400,150);
        setSize(600,500);

        //paramètres initiaux
        mMinutes = mMin;
        mSecondes = mSec;
        secondes = 1;
        optionJoueur = -1;
        gameOver = false;
        nbreMines = 10;
        nbreDrapeaux = nbreMines;
        points = 0;
        
        //initialisation des cellules
        listeCellules = new cellule[8][8];
        for (int i=0; i<8;i++)
        {
            for (int j=0; j<8;j++)
            {
                listeCellules[i][j] = new cellule(i,j);
            }
        }
        
        //initialisation des mines
        for (int i=0; i<nbreMines; i++)
        {
            do{
                randomI = (int)(Math.random()*8);
                randomJ = (int)(Math.random()*8);
            }while(listeCellules[randomI][randomJ].mine);

            listeCellules[randomI][randomJ].mine = true;
        }
        
        //calcul du nombre de mines voisines
        for (int i=0; i<8;i++)
        {
            for (int j=0; j<8;j++)
            {
                listeCellules[i][j].compte(listeCellules);

                //relance le jeu si le chiffre est supérieur à 3
                if (listeCellules[i][j].chiffre > 3)
                {
                    optionJoueur = 0;
                }
            }
        }

        //fin d'initialisation de la fenêtre
        tempsPartie = new Timer(1000,this);
        chrono = new Timer(30,this);
        tempsPartie.start();
        chrono.start();
        addMouseListener(this);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //CHRONO
    public void actionPerformed(ActionEvent e)
    {
        //gestion du chronomètre
        if (e.getSource() == tempsPartie)
        {
            secondes++;
            if (secondes%60 == 0)
            {
                secondes = 0;
                minutes++;
            }
        }

        //boucle principale
        else if (e.getSource() == chrono)
        {
            //partie terminée, le joueur veut rejouer
            if (optionJoueur == 0)
            {
                setVisible(false);
                fenetreJeu fen = new fenetreJeu(8, mMinutes, mSecondes);
                optionJoueur = -1;
                nbreDrapeaux = nbreMines;
                points = 0;
                gameOver = false;
            }
            
            //partie terminée, le joueur veut quitter
            else if (optionJoueur == 2)
            {
                setVisible(false);
                gameOver = false;
                points = 0;
            }

            //condition de réussite
            if (points == nbreMines)
            {
                repaint();

                for (int i=0; i<8; i++)
                {
                    for (int j=0; j<8; j++)
                    {
                        if (!listeCellules[i][j].drapeau)
                            listeCellules[i][j].revelee = true;
                    }
                }
                        
                tempsPartie.stop();
                gameOver = true;
                
                optionJoueur = JOptionPane.showConfirmDialog(null,"GAGNE ! temps : "+minutes+" min "+secondes+" sec", "fin du jeu", 2);

                //calcul du highscore
                if (minutes <= mMinutes)
                {
                    if (secondes <= mSecondes)
                    {
                        mMinutes = minutes;
                        mSecondes = secondes;
                    }
                }
            }

            //gestion des cellules
            for (int i=0; i<8; i++)
            {
                for (int j=0; j<8; j++)
                {
                    //révèle tout le plateau en cas de fin de partie
                    if (gameOver)
                    {
                        //chrono.stop();
                        if (!listeCellules[i][j].drapeau)
                            listeCellules[i][j].revelee = true;
                    }
                    
                    //fait apparaître les cellules voisines d'une cellule "vide"
                    if (listeCellules[i][j].revelee && listeCellules[i][j].chiffre == 0)
                    {
                        listeCellules[i][j].reveleVoisin(listeCellules);
                    }  
                }
            }
            repaint();
        }
    }
    
    //DESSIN
    public void paint(Graphics g)
    {
        //affichage des drapeaux restants et du chronomètre
        g.clearRect(455,50, getWidth()-455,getHeight()-50);
        g.setFont(new Font("TimesRoman", Font.BOLD, 12));
        g.drawString("CHRONO", 470, 70);
        g.drawString(minutes+ " min, " +secondes+" sec", 470, 85);
        g.drawString("DRAPEAUX", 470, 125);
        g.drawString(nbreDrapeaux+ " / 10",470,140);
        g.drawString("HIGHSCORE", 470, 180);
        
        if (mMinutes!=300 && mSecondes!=300)
            g.drawString(mMinutes+" min, "+mSecondes+" sec", 470, 195);

        //affichage des cellules
        for (int i=0; i<8;i++)
        {
            for (int j=0; j<8;j++)
            {
                if (listeCellules[i][j].drapeau)
                    g.drawImage(dr,listeCellules[i][j].x,listeCellules[i][j].y,this);

                if (!listeCellules[i][j].revelee && !listeCellules[i][j].drapeau)
                    g.drawImage(pR,listeCellules[i][j].x,listeCellules[i][j].y,this);

                else if (listeCellules[i][j].revelee)
                {
                    if (listeCellules[i][j].mine)
                        g.drawImage(m,listeCellules[i][j].x,listeCellules[i][j].y,this);
                    else
                    {
                        switch(listeCellules[i][j].chiffre)
                        {
                            case 0:
                                g.drawImage(r,listeCellules[i][j].x,listeCellules[i][j].y,this);
                                break;
                            case 1:
                                g.drawImage(i1,listeCellules[i][j].x,listeCellules[i][j].y,this);
                                break;
                            case 2:
                                g.drawImage(i2,listeCellules[i][j].x,listeCellules[i][j].y,this);
                                break;
                            case 3:
                                g.drawImage(i3,listeCellules[i][j].x,listeCellules[i][j].y,this);
                                break;
                            default:
                                g.drawImage(r,listeCellules[i][j].x,listeCellules[i][j].y,this);
                                break;
                        }
                    }
                }
                listeCellules[i][j].dessine(g);
            }
        }
    }

    //SOURIS
    public void mouseClicked(MouseEvent e)
    {
        for (int i=0; i<8;i++)
        {
            for (int j=0; j<8;j++)
            {
                int cellX = listeCellules[i][j].x;
                int cellY = listeCellules[i][j].y;
                int cellC = listeCellules[i][j].cote;

                if (e.getX() > cellX && e.getX() < cellX + cellC && e.getY() > cellY && e.getY() < cellY + cellC)
                {
                    if (e.getButton() == MouseEvent.BUTTON3 && !listeCellules[i][j].drapeau && nbreDrapeaux > 0)
                    {
                        listeCellules[i][j].drapeau = true;
                        nbreDrapeaux--;
                        if (listeCellules[i][j].mine)
                            points++;

                    }
                    else if (e.getButton() == MouseEvent.BUTTON3 && listeCellules[i][j].drapeau)
                    {
                        listeCellules[i][j].drapeau = false;
                        nbreDrapeaux++;
                        if (listeCellules[i][j].mine)
                            points--;
                    }

                    if (e.getButton() == MouseEvent.BUTTON1 && !listeCellules[i][j].drapeau) 
                    {
                        listeCellules[i][j].revelee = true;
                        if (listeCellules[i][j].mine)
                        {
                            tempsPartie.stop();
                            gameOver = true;  
                            optionJoueur = JOptionPane.showConfirmDialog(null,"PERDU ! temps : "+minutes+" min "+secondes+" sec", "fin du jeu", 2);
                        }
                    }
                }
            }
        }
    }

    //méthodes surchargées pour la souris
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
}