package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SMDApplication {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    @Inject
    private EntityManagerFactory emFactory;

    @Inject
    private EntityManager em;

    @Inject
    ReleaseRepository releaseRepository;

    @Inject
    MediaImportManager mediaImportManager;

    @Inject
    @Named("mediaimport")
    ExecutorService mediaImportService;

    public static void main(String[] args) {
        new SMDApplication();
    }

    public SMDApplication() {
        try {
            DatabaseProvider provider = null;
            String database = System.getProperty("org.socialmusicdiscovery.server.database");
            if(database!=null) {
                provider = InjectHelper.instanceWithName(DatabaseProvider.class,database);
                if(provider == null) {
                    throw new RuntimeException("No database provider exists for: "+database);
                }
            }else {
                provider = InjectHelper.instanceWithName(DatabaseProvider.class,"derby");
            }
            provider.start();

            InjectHelper.injectMembers(this);

            Collection<Release> releases = releaseRepository.findAll();
            if (releases.size() > 0) {
                System.out.println("\nPrinting all available releases in database, please wait...\n");
            } else {
                System.out.println("\nNo releases available in database!");
            }
            for (Release release : releases) {
                printRelease(release);
            }

            Map<String, String> initParams = new HashMap<String, String>();
            initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api");

            System.out.println("Starting grizzly...");
            URI uri = UriBuilder.fromUri("http://localhost/").port(9998).build();
            SelectorThread threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);
            System.out.println(String.format("Try out %spersons\nHit enter to stop it...", uri));
            System.in.read();
            threadSelector.stopEndpoint();

            System.out.println("\n\nExiting...\n");

            for(MediaImportStatus module: mediaImportManager.getRunningModules()) {
                mediaImportManager.abortImport(module.getModule());
            }
            mediaImportService.shutdown();
            if(!mediaImportService.awaitTermination(10, TimeUnit.SECONDS)) {
                mediaImportService.shutdownNow();
            }
            
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emFactory != null && emFactory.isOpen()) {
                emFactory.close();
            }
            provider.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printRelease(Release release) {
        if (release != null) {
            String label = "";
            if (release.getLabel() != null) {
                label = "(" + release.getLabel().getName() + ")";
            }
            String date = "";
            if (release.getDate() != null) {
                date = " (" + DATE_FORMAT.format(release.getDate()) + ")";
            }
            System.out.println(release.getName() + date + " " + label);
            System.out.println("-------------------------------");
            for (Contributor contributor : release.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            if (release.getContributors().size() > 0) {
                System.out.println();
            }
            if(release.getMediums().size()>0) {
                for (Medium medium : release.getMediums()) {
                    printTracks(medium.getTracks(),(medium.getName()!=null?medium.getName():""+medium.getNumber())+" - ");
                }
            }else {
                printTracks(release.getTracks(),"");
            }
            System.out.println();
        }
    }
    private static void printTracks(List<Track> tracks, String prefix) {
        for (Track track : tracks) {
            Recording recording = track.getRecording();
            Work work = recording.getWork();

            System.out.println(prefix+track.getNumber() + ". " + work.getName());
            for (Contributor contributor : recording.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            for (Contributor contributor : work.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            //System.out.println();
        }
    }
}
