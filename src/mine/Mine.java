package mine;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;



public class Mine extends JFrame implements ActionListener,MouseListener{
	boolean dead,finish;
	int mineLeft,blankLeft;
	long time = 0;
	ArrayList<Integer> mine = new ArrayList<Integer>();
	JPanel p = new JPanel(new BorderLayout());
	JPanel p1 = new JPanel();
	JTextField jt1 = new JTextField("10",4),jt2 = new JTextField("6",4),jt3 = new JTextField("7",4),jt4 = new JTextField("7",4);
	JLabel jl1 = new JLabel("rows:"),jl2 = new JLabel("columns:"),jl3 = new JLabel("set mine:"),jl4 = new JLabel("mine left:");
	JButton commit = new JButton("reset");
	public Mine(){
		p1.add(jl1);
		p1.add(jt1);
		p1.add(jl2);
		p1.add(jt2);
		p1.add(jl3);
		p1.add(jt3);
		p1.add(jl4);
		p1.add(jt4);
		commit.addActionListener(this);
		p1.add(commit);
		p.add(p1,BorderLayout.NORTH);
		setContentPane(p);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,600,400);
		setVisible(true);
	}
	
	class Block extends JButton{
		boolean mine,marked;
		int num;
		ArrayList<Block> surround = new ArrayList<Block>();
		public Block(){
			super();
		}
		public Block(String name){
			super(name);
		}
		public void step(){
			if(mine){
				setEnabled(false);
				setText("*");
				dead = true;
				showMine();
				JOptionPane.showMessageDialog(Mine.this, "you are dead...");
			}
			else{
				this.traverse();
			}
		}
		public void traverse(){
			if(isEnabled()){
				setEnabled(false);
				if(num==0){
					Iterator<Block> it = surround.iterator();
					while(it.hasNext()){
						Block next = it.next();
						if(next.isEnabled()){
							next.traverse();
						}
					}
				}else{
					setText(((Integer)num).toString());
				}
				blankLeft--;
			}
		}
		public void clear() {
			int count = 0;
			if(!isEnabled()){
				Iterator<Block> it = surround.iterator();
				while(it.hasNext()){
					if(it.next().marked)count++;
				}
				if(count==num){
					it = surround.iterator();
					while(it.hasNext()){
						Block next = it.next();
						if(next.isEnabled()&&!next.marked)next.step();
					}
				}
			}
		}
	}
	
	public void init(int x,int y,int z) {
		JPanel p2 = new JPanel(new GridLayout(y,x));
		p.add(p2,BorderLayout.CENTER);
		mine.clear();
		dead = false;
		mineLeft = z;
		blankLeft = x*y;
		jt4.setText(((Integer)mineLeft).toString());
		
		for(int i=0;i<x*y;i++){
			Block block = new Block();
			//block.addActionListener(this);
			block.addMouseListener(this);
			p2.add(block);
		}
		
		for(int i=0;i<x*y;i++){
			Block block = (Block)p2.getComponent(i);
			if(i-x>-1){
				block.surround.add((Block)p2.getComponent(i-x));
				if((i-x)%x!=0){
					block.surround.add((Block)p2.getComponent(i-x-1));
				}
				if((i-x+1)%x!=0){
					block.surround.add((Block)p2.getComponent(i-x+1));
				}
			}
			if(i+x<x*y){
				block.surround.add((Block)p2.getComponent(i+x));
				if((i+x)%x!=0){
					block.surround.add((Block)p2.getComponent(i+x-1));
				}
				if((i+x+1)%x!=0){
					block.surround.add((Block)p2.getComponent(i+x+1));
				}
			}
			if(i%x!=0){
				block.surround.add((Block)p2.getComponent(i-1));
			}
			if((i+1)%x!=0){
				block.surround.add((Block)p2.getComponent(i+1));
			}
		}
		
		Random rad = new Random();
		for(int i=0;i<z;i++){
			int r;
			do{
				r = rad.nextInt(x*y);
			}while(((Block)p2.getComponent(r)).mine);
			((Block)p2.getComponent(r)).mine = true;
			mine.add(r);
		}
		
		for(int i=0;i<x*y;i++){
			int count = 0;
			Block block = (Block)p2.getComponent(i);
			if(block.mine){
				block.num = -1;
			}else{
				Iterator<Block> it = block.surround.iterator();
				while(it.hasNext()){
					if(it.next().mine)count++;
				}
				block.num = count;
			}
		}
	}
	
	public void showMine(){
		Iterator<Integer> it = mine.iterator();
		while(it.hasNext()){
			((Block)((JPanel)p.getComponent(1)).getComponent(it.next())).setText("*");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		p.remove(1);
		init(Integer.parseInt(jt1.getText()),Integer.parseInt(jt2.getText()),Integer.parseInt(jt3.getText()));
		p.validate();
		dead = false;
		finish = false;
	}

	public void mouseClicked(MouseEvent e) {
		if(!dead&&!finish){
			if(e.getModifiers()==InputEvent.BUTTON1_MASK){
				((Block)e.getSource()).step();
			}else if(e.getModifiers()==InputEvent.BUTTON3_MASK){
				Block temp = (Block)e.getSource();
				if(temp.isEnabled()){
					if(!temp.marked){
						temp.marked = true;
						temp.setText("M");
						mineLeft--;
						blankLeft--;
						jt4.setText(((Integer)mineLeft).toString());
					}else{
						temp.marked = false;
						temp.setText("");
						mineLeft++;
						blankLeft++;
						jt4.setText(((Integer)mineLeft).toString());
					}
				}
			}
			if(blankLeft==0&&mineLeft==0){
				finish = true;
				JOptionPane.showMessageDialog(Mine.this, "congrads!");
			}
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		if(!dead&&!finish){
			long time = e.getWhen();
			if(time-this.time<100){
				((Block)e.getSource()).clear();
				if(blankLeft==0&&mineLeft==0){
					finish = true;
					JOptionPane.showMessageDialog(Mine.this, "congrads!");
				}
			}
			this.time = time;
		}
	}
	
	public void mouseReleased(MouseEvent e) {}
	
	public static void main(String[] args) {
		Mine mine = new Mine();
		mine.init(10,6,7);
	}
}
