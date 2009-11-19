package org.projectusus.core.internal.proportions.sqi.acd;

import java.util.ArrayList;
import java.util.List;

public class AcdModel {

    // TODO Hashtable verwenden??
    List<ILinkedNode> classes = new ArrayList<ILinkedNode>();

    public void addClassReference( String referencingClass, String referencedClass ) {
        ILinkedNode startNode = findOrAdd( referencingClass );
        ILinkedNode endNode = findOrAdd( referencedClass );

        if( (startNode != endNode) && !startNode.getChildren().contains( endNode ) ) {
            startNode.getChildren().add( endNode );
        }
    }

    private ILinkedNode findOrAdd( String id ) {
        for( ILinkedNode node : classes ) {
            if( node.getName().equals( id ) ) {
                return node;
            }
        }
        return addNode( id );
    }

    private ILinkedNode addNode( String id ) {
        ILinkedNode newNode = new ClassNode( id );
        classes.add( newNode );
        return newNode;
    }

    // / <summary>
    // / The relative ACD of a system with n components is ACD/n.
    // / Thus it is a percentage value in range [0%, 100%].
    // / </summary>
    // / <returns></returns>
    public double getRelativeACD() {
        if( classes.size() == 0 ) {
            return 0.0;
        }
        return getACD() / classes.size();
    }

    // / <summary>
    // / The average component dependency (ACD) of a system with n components is
    // CCD/n.
    // / </summary>
    // / <returns></returns>
    private double getACD() {
        if( classes.size() == 0 ) {
            return 0.0;
        }
        return getCCD() / (double)classes.size();
    }

    // / <summary>
    // / The cumulative component dependency (CCD) of a (sub)system is the sum
    // over all
    // / components Ci of the (sub)system of the number of components needed to
    // test each Ci incrementally.
    // / </summary>
    // / <returns></returns>
    private int getCCD() {
        int allDependencies = 0;
        for( ILinkedNode node : classes ) {
            allDependencies += getCCD( node );
        }
        return allDependencies;
    }

    // / <summary>
    // / The cumulative component dependency (CCD) of a (sub)system is the sum
    // over all
    // / components Ci of the (sub)system of the number of components needed to
    // test each Ci incrementally.
    // / </summary>
    // / <param name="node">starting node for subsystem to analyze</param>
    // / <returns></returns>
    public int getCCD( ILinkedNode node ) {
        clearAllNodes(); // in markedNodeCount hineinziehen
        markReferencedNodes( node );
        return getMarkedNodeCount();
    }

    public int getCCD( String fullyQualifiedName ) {
        ILinkedNode node = findOrAdd( fullyQualifiedName );
        return getCCD( node );
    }

    private int getMarkedNodeCount() {
        int markedNodes = 0;
        for( ILinkedNode node : classes ) {
            if( node.isMarked() ) {
                markedNodes++;
            }
        }
        return markedNodes;
    }

    // Knoten durch Einf�gen in Set markieren, damit Z�hlen + L�schen in O(1)
    private void markReferencedNodes( ILinkedNode node ) {
        if( node.isMarked() ) {
            return;
        }
        node.setMarked( true );
        for( ILinkedNode childNode : node.getChildren() ) {
            markReferencedNodes( childNode );
        }
    }

    private void clearAllNodes() {
        for( ILinkedNode node : classes ) {
            node.setMarked( false );
        }
    }

}
