package com.mola.weka.managers;

import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.instruments.Quote;
import com.mola.model.AbstractModel;
import com.mola.persistence.managers.PersistenceManager;
import com.mola.trade.Trade;
import com.mola.weka.models.WekaArffModel;
import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.List;

public class WekaPersistenceManager implements PersistenceManager {

    private Session session;

    public WekaPersistenceManager() {
        initialize();
    }

    public WekaPersistenceManager initialize() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        SessionFactory factory = configuration.addPackage("com.ts.weka.arff")
                .addAnnotatedClass(WekaArffModel.class)
                .addPackage("com.ts.instrument").addAnnotatedClass(Quote.class)
                .configure("hibernate.cfg.xml").buildSessionFactory();
        // Iterator<PersistentClass> classMappings =
        // configuration.getClassMappings();
        session = factory.openSession();
        return this;
    }

    @Override
    public void saveOrUpdate(Object object) {

    }

    public void persistModel(AbstractModel model) {
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.persist((WekaArffModel) model);
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

    @SuppressWarnings("unchecked")
    public Object getChart(Pair pair, Granularity granularity,
                           ChartType[] chartTypes) {
        StringBuilder hql = new StringBuilder(
                "FROM WekaArffModel m WHERE m.instrument=:pair and m.granularity=:granularity");
        if (chartTypes != null && chartTypes.length > 0) {
            for (int i = 0; i < chartTypes.length; ++i) {
                if (chartTypes[i] == ChartType.candles) {
                    continue;
                }
                hql.append(" and m." + chartTypes[i] + " is not null");
            }
        }

        hql.append(" order by m.id desc limit 1");
        Query query = session.createQuery(hql.toString())
                .setParameter("granularity", granularity.name())
                .setParameter("pair", pair.name());
        List<WekaArffModel> results = query.list();
        return results;
    }

    @Override
    public Object getTrade() {
        return null;
    }

    @Override
    public List<Trade> getOpenTrades() {
        return null;
    }
}