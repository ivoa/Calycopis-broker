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

import org.apache.commons.lang3.NotImplementedException;

import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper class for a List to transform from one type to another.
 * 
 */
@Slf4j
public class CollectionWrapper<Outer, Inner>
    implements Collection<Outer>
    {
    /**
     * The List we are wrapping. 
     */
    final Collection<Inner> inner ;

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
    protected CollectionWrapper(final Collection<Inner> inner)
        {
        this.inner = inner;
        }

    @Override
    public int size()
        {
        return inner.size();
        }

    @Override
    public boolean isEmpty()
        {
        return inner.isEmpty();
        }

    @Override
    public boolean contains(Object object)
        {
        return inner.contains(object);
        }

    @Override
    public Iterator<Outer> iterator()
        {
        return new Iterator<Outer>()
            {
            final Iterator<Inner> inneriter = inner.iterator();
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
        return inner.add(
            unwrap(
                outer
                )
            );
        }

    @Override
    public boolean remove(Object object)
        {
        return inner.remove(
            object
            );
        }

    @Override
    public boolean containsAll(Collection<?> c)
        {
        return inner.containsAll(c);
        }

    @Override
    public boolean addAll(Collection<? extends Outer> c)
        {
        throw new NotImplementedException(
            "addAll(Collection<? extends Outer>) is not implemented"
            );
        }

    @Override
    public boolean removeAll(Collection<?> c)
        {
        return inner.removeAll(c);
        }

    @Override
    public boolean retainAll(Collection<?> c)
        {
        return inner.retainAll(c);
        }

    @Override
    public void clear()
        {
        inner.clear();
        }
    }
