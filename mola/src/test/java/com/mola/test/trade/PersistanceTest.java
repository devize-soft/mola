package com.mola.test.trade;

import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.trade.Trade;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.Date;

/**
 * Created by bilgi on 4/3/15.
 */
public class PersistanceTest {

    private Session session;

    public static void main(String[] args){
        new PersistanceTest().run();
    }

    public PersistanceTest(){
        initialize();
    }

    private PersistanceTest initialize(){
        buildTrade();
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        SessionFactory factory = configuration.addPackage("com.mola.trade")
                .addAnnotatedClass(Trade.class)
//                .addPackage("com.ts.instrument").addAnnotatedClass(Trade.class)
                .configure("hibernate.cfg.xml").buildSessionFactory();
        // Iterator<PersistentClass> classMappings =
        // configuration.getClassMappings();
        session = factory.openSession();
        return this;
    }

    public void run(){
        Trade trade = buildTrade();
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.persist(trade);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null)
                    tx.rollback();
                e.printStackTrace();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private Trade buildTrade() {
        Trade trade = new Trade();
        trade.setActive(true);
        trade.setEntry(1.2001);
        trade.setGranularity("M1");
        trade.setStoploss(1.1996);
        trade.setProvider("oanda");
        trade.setPosition(true);
        trade.setTakeProfit(1.2015);
        trade.setOrderId(18038454512L);
        trade.setGranularity(Granularity.M1.name());
        trade.setPair(Pair.EUR_USD.getNameFormatted());
        trade.setTickTime(new Date());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        return trade;
    }
}
