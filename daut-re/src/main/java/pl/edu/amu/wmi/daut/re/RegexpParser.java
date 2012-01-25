/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.amu.wmi.daut.re;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author kacper
 */
public class RegexpParser {

    RegexpOperatorTree parse(String regexp, RegexpOperatorManager manager) {

        StringBuilder stringBuilder = new StringBuilder(regexp);
        String tmp = stringBuilder.toString();
        ArrayList<RegexpOperator> regexpOperators = new ArrayList<RegexpOperator>();
        Stack<Moja> regexpOperatorsStack = new Stack<Moja>();
        List<String> ids = null;
        String actualId = null;

        while (!tmp.isEmpty()) {
            ids = manager.getOperatorsForStringPrefix(tmp);
            if (ids.size() == 1) {

                actualId = ids.get(0);
                int priority = manager.getPriority(actualId);
                List<String> separators = manager.getSeparators(actualId);
                RegexpOperator ro = manager.getFactory(actualId).createOperator(
                        new LinkedList<String>());
                Moja moja = new Moja(priority, ro);

                if (regexpOperatorsStack.isEmpty()
                        || regexpOperatorsStack.peek().getPriority() < moja.
                        getPriority()) {
                    regexpOperatorsStack.push(moja);
                } else if (regexpOperatorsStack.peek().getPriority() >= moja.
                        getPriority()) {
                    Moja tmpMoja = regexpOperatorsStack.pop();
                    regexpOperators.add(tmpMoja.getRegexpOperator());

                }
                stringBuilder = stringBuilder.delete(0,separators.get(0).length()-1);
                tmp = stringBuilder.toString();
            }
        }

        while (!regexpOperatorsStack.isEmpty()) {
            regexpOperators.add(regexpOperatorsStack.pop().getRegexpOperator());
        }

        Stack<RegexpOperatorTree> stack = new Stack<RegexpOperatorTree>();
        for (RegexpOperator ro : regexpOperators) {
            if (ro.arity() == 0) {
                stack.push(new RegexpOperatorTree(ro,
                        new LinkedList<RegexpOperatorTree>()));
            } else {
                LinkedList<RegexpOperatorTree> ll = new LinkedList<RegexpOperatorTree>();
                for (int i = 0; i < ro.arity(); ++i) {
                    ll.add(stack.pop());
                }
                stack.push(new RegexpOperatorTree(ro, ll));
            }
        }
        return stack.pop();

        /*RegexpOperatorTree rot1 = null, rot2 = null;
        for (RegexpOperator ro : regexpOperators) {
        if (ro.arity() == 0) {
        if (rot1 == null) {
        rot1 = new RegexpOperatorTree(ro, new LinkedList<RegexpOperatorTree>());
        } else {
        rot2 = new RegexpOperatorTree(ro, new LinkedList<RegexpOperatorTree>());
        }
        } else if (ro.arity() == 1) {
        if (!(rot2 == null)) {
        LinkedList<RegexpOperatorTree> ll = new LinkedList<RegexpOperatorTree>();
        ll.add(rot2);
        rot2 = new RegexpOperatorTree(ro, ll);
        } else {
        LinkedList<RegexpOperatorTree> ll = new LinkedList<RegexpOperatorTree>();
        ll.add(rot1);
        rot1 = new RegexpOperatorTree(ro, ll);
        }
        } else if (ro.arity() == 2) {
        LinkedList<RegexpOperatorTree> ll = new LinkedList<RegexpOperatorTree>();
        ll.add(rot1);
        ll.add(rot2);
        rot1 = new RegexpOperatorTree(ro, ll);
        rot1 = null;
        rot2 = null;
        }
        }*/

        //return null;
    }

    class Moja {

        private int priority;
        private RegexpOperator regExpOper;

        public Moja(int priority, RegexpOperator regExpOper) {
            this.priority = priority;
            this.regExpOper = regExpOper;
        }

        public int getPriority() {
            return priority;
        }

        public RegexpOperator getRegexpOperator() {
            return regExpOper;
        }
    }
}
