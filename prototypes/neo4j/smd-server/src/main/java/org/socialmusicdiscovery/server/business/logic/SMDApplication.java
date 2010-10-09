package org.socialmusicdiscovery.server.business.logic;

import jo4neo.ObjectGraph;
import jo4neo.ObjectGraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.socialmusicdiscovery.server.business.model.core.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class SMDApplication {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    public static void main(String[] args) {
        ObjectGraph em;
        GraphDatabaseService emFactory;

        try {
            emFactory = new EmbeddedGraphDatabase("smd-database");
            em = ObjectGraphFactory.instance().get(emFactory);

            Collection<Release> releases = em.get(Release.class);
            if(releases.size()>0) {
                System.out.println("\nPrinting all available releases in database, please wait...\n");
            }else {
                System.out.println("\nNo releases available in database!");
            }
            for(Release release: releases) {
                printRelease(release);
            }
            System.out.println("\n\nExiting...\n");

            em.close();
            emFactory.shutdown();
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
