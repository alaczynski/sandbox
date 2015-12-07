package org.asl.spock

import spock.lang.Ignore
import spock.lang.Specification

class SpockInteractionTest extends Specification {

    /* mock creation **************************************************************************************************/

    def 'mock - create with provided type'() {
        given:
        def repository = Mock(Repository)

        expect:
        repository instanceof Repository
    }

    def 'mock - create with type inferred from variable'() {
        given:
        Repository repository = Mock()

        expect:
        repository instanceof Repository
    }

    /* default behaviour **********************************************************************************************/
    // method returns false, 0, or null
    def 'mock - default behaviour'() {
        given:
        def repository = Mock(Repository)

        expect:
        repository.getById(1) == null
        repository.exists(1) == false
        repository.count() == 0
    }
    /* stub ***********************************************************************************************************/
    /**
     * Stubbing is the act of making collaborators respond to method calls in a certain way.
     * When stubbing a method, you don’t care if and how many times the method is going to be called;
     * you just want it to return some value, or perform some side effect, whenever it gets called.
     */
    def 'stub - basic'() {
        given:
        def repository = Stub(Repository)
        repository.getById(1) >> ENTITY_1

        when:
        def entity = repository.getById(1)

        then:
        entity == ENTITY_1
    }

    @Ignore('InvalidSpecException is thrown')
    def 'stub - interaction cannot be verified'() {
        given:
        def repository = Stub(Repository)
        repository.getById(1) >> ENTITY_1

        when:
        repository.getById(1)

        then:
        1 * repository.getById(1)
    }

    /** ****************************************************************************************************************/
    static final ENTITY_1 = new Entity(id: 1)

    static class Repository {
        void save(Entity entity) {

        }

        Entity getById(int id) {
            return null
        }

        boolean exists(int id) {
            true
        }

        int count() {
            -1
        }
    }

    static class Entity {
        int id
    }
}
