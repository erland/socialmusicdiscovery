package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.business.model.core.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class SMDApplication {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    public static void main(String[] args) {
        EntityManagerFactory emFactory;
        EntityManager em;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DriverManager.getConnection("jdbc:derby:smd-database;create=true").close();

            emFactory = Persistence.createEntityManagerFactory("smd");
            em = emFactory.createEntityManager();

            Query query = em.createQuery("from Release");
            List<Release> releases = (List<Release>) query.getResultList();
            if(releases.size()>0) {
                System.out.println("\nPrinting all available releases in database, please wait...\n");
            }else {
                System.out.println("\nNo releases available in database!");
            }
            for(Release release: releases) {
                printRelease(release);
            }
            System.out.println("\n\nExiting...\n");

            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emFactory != null && emFactory.isOpen()) {
                emFactory.close();
            }
            try {
                DriverManager.getConnection("jdbc:derby:smd-database;shutdown=true").close();
            } catch (SQLNonTransientConnectionException ex) {
                if (ex.getErrorCode() != 45000) {
                    throw ex;
                }
                // Shutdown success
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void printRelease(Release release) {
        if(release != null) {
            String label = "";
            if(release.getLabel() != null) {
                label = "("+release.getLabel().getName()+")";
            }
            String date = "";
            if(release.getDate()!=null) {
                date = " ("+DATE_FORMAT.format(release.getDate())+")";
            }
            System.out.println(release.getName()+ date+" "+label);
            System.out.println("-------------------------------");
            for (Contributor contributor: release.getContributors()) {
                if(contributor.getArtist().getPerson() != null) {
                    System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName()+" ("+contributor.getArtist().getPerson().getName()+")");
                }else {
                    System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName());
                }
            }
            if(release.getContributors().size()>0) {
                System.out.println();
            }
            for (Track track: release.getTracks()) {
                Recording recording = track.getRecording();
                Work work = recording.getWork();

                System.out.println(track.getNumber()+". "+work.getName());
                for (Contributor contributor: recording.getContributors()) {
                    if(contributor.getArtist().getPerson() != null) {
                        System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName()+" ("+contributor.getArtist().getPerson().getName()+")");
                    }else {
                        System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName());
                    }
                }
                for (Contributor contributor: work.getContributors()) {
                    if(contributor.getArtist().getPerson() != null) {
                        System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName()+" ("+contributor.getArtist().getPerson().getName()+")");
                    }else {
                        System.out.println("- "+contributor.getType()+": "+contributor.getArtist().getName());
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
