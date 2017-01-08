package com.mola.persistence.managers;

import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.model.AbstractModel;
import com.mola.trade.Trade;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by bilgi on 4/6/15.
 */
@Service
public class MolaPersistenceManager implements PersistenceManager {


    private Session session;

    public MolaPersistenceManager(){

    }

    @PostConstruct
    public void init(){
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        SessionFactory factory = configuration.addPackage("com.mola.trade")
                .addAnnotatedClass(Trade.class)
                .configure("hibernate.cfg.xml").buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public void saveOrUpdate(Object object) {
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(object);
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

    @Override
    public void persistModel(AbstractModel model) {

    }

    @Override
    public Object getChart(Pair pair, Granularity granularity, ChartType[] chartTypes) {
        return null;
    }

    @Override
    public Object getTrade() {
        return null;
    }

    @Override
    public List<Trade> getOpenTrades() {
        StringBuilder hql = new StringBuilder(
                "FROM Trade t WHERE t.active=1 order by t.tickTime asc");
        Query query = session.createQuery(hql.toString());
        List<Trade> results = query.list();
        return results;
    }
}
