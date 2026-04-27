/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 *
 */

package net.ivoa.calycopis.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.NotImplementedException;

import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper class for a List to transform from one type to another.
 * 
 */
@Slf4j
public class ListWrapper<Outer, Inner>
    implements List<Outer>
    {
    /**
     * The List we are wrapping. 
     */
    final List<Inner> innerlist ;

    /**
     * A method to wrap an Inner as an Outer.
     * You need to override this to make the ListWrapper functional.
     * 
     */
    public Outer wrap(Inner inner)
        {
        throw new NotImplementedException(
            "wrap(Inner) is not implemented"
            );
        }

    /**
     * A method to unwrap an Inner from an Outer.
     * 
     */
    public Inner unwrap(Outer outer)
        {
        throw new NotImplementedException(
            "unwrap(Outer) is not implemented"
            );
        }

    /**
     * Protected constructor.
     *  
     */
    protected ListWrapper(final List<Inner> inner)
        {
        this.innerlist = inner;
        }

    @Override
    public int size()
        {
        return innerlist.size();
        }

    @Override
    public boolean isEmpty()
        {
        return innerlist.isEmpty();
        }

    @Override
    public boolean contains(Object object)
        {
        return innerlist.contains(object);
        }

    @Override
    public Iterator<Outer> iterator()
        {
        return new Iterator<Outer>()
            {
            final Iterator<Inner> inneriter = innerlist.iterator();
            @Override
            public boolean hasNext()
                {
                return inneriter.hasNext();
                }
            @Override
            public Outer next()
                {
                return wrap(
                    inneriter.next()
                    );
                }
            };
        }

    @Override
    public Object[] toArray()
        {
        throw new NotImplementedException(
            "toArray() is not implemented"
            );
        }

    @Override
    public <T> T[] toArray(T[] a)
        {
        throw new NotImplementedException(
            "toArray(T[]) is not implemented"
            );
        }

    @Override
    public boolean add(Outer outer)
        {
        return innerlist.add(
            unwrap(
                outer
                )
            );
        }

    @Override
    public boolean remove(Object object)
        {
        return innerlist.remove(
            object
            );
        }

    @Override
    public boolean containsAll(Collection<?> c)
        {
        return innerlist.containsAll(c);
        }

    @Override
    public boolean addAll(Collection<? extends Outer> c)
        {
        throw new NotImplementedException(
            "addAll(Collection<? extends Outer>) is not implemented"
            );
        }

    @Override
    public boolean addAll(int index, Collection<? extends Outer> c)
        {
        throw new NotImplementedException(
            "addAll(int, Collection<? extends Outer>) is not implemented"
            );
        }

    @Override
    public boolean removeAll(Collection<?> c)
        {
        return innerlist.removeAll(c);
        }

    @Override
    public boolean retainAll(Collection<?> c)
        {
        return innerlist.retainAll(c);
        }

    @Override
    public void clear()
        {
        innerlist.clear();
        }

    @Override
    public Outer get(int index)
        {
        return this.wrap(
            this.innerlist.get(
                index
                )
            );
        }

    @Override
    public Outer set(int index, Outer outer)
        {
        return wrap(
            this.innerlist.set(
                index,
                this.unwrap(
                    outer
                    )
                )
            );
        }

    @Override
    public void add(int index, Outer outer)
        {
        this.innerlist.add(
            index,
            this.unwrap(
                outer
                )
            );
        }

    @Override
    public Outer remove(int index)
        {
        return wrap(
            innerlist.remove(index)
            );
        }

    @Override
    public int indexOf(Object object)
        {
        return innerlist.indexOf(
            object
            );
        }

    @Override
    public int lastIndexOf(Object object)
        {
        return innerlist.lastIndexOf(
            object
            );
        }

    @Override
    public ListIterator<Outer> listIterator()
        {
        log.debug("listIterator()");
        return new ListIterator<Outer>()
            {
            final ListIterator<Inner> inneriter = innerlist.listIterator();

            @Override
            public boolean hasNext()
                {
                return inneriter.hasNext();
                }

            @Override
            public Outer next()
                {
                return wrap(
                    inneriter.next()
                    );
                }

            @Override
            public boolean hasPrevious()
                {
                return inneriter.hasPrevious();
                }

            @Override
            public Outer previous()
                {
                return wrap(
                    inneriter.previous()
                    );
                }

            @Override
            public int nextIndex()
                {
                return inneriter.nextIndex();
                }

            @Override
            public int previousIndex()
                {
                return inneriter.previousIndex();
                }

            @Override
            public void remove()
                {
                inneriter.remove();
                }

            @Override
            public void set(Outer outer)
                {
                inneriter.set(
                    unwrap(outer)
                    );
                }

            @Override
            public void add(Outer outer)
                {
                inneriter.add(
                    unwrap(outer)
                    );
                }
            };
        }

    @Override
    public ListIterator<Outer> listIterator(int index)
        {
        throw new NotImplementedException(
            "listIterator(int) is not implemented"
            );
        }

    @Override
    public List<Outer> subList(int fromIndex, int toIndex)
        {
        throw new NotImplementedException(
            "listIterator(int,int) is not implemented"
            );
        }
    }
