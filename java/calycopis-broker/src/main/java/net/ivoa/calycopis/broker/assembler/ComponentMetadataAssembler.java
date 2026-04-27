/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-04-27T13:46:34",
 *     "name": "Copilot",
 *     "version": "unknown",
 *     "model": "claude-sonnet-4.5",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.assembler;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.message.MessageItemBean;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * Assembler that converts a {@link ComponentEntity} to an
 * {@link IvoaComponentMetadata} response bean.
 *
 * This class holds the logic that was formerly inline in
 * {@code ComponentEntity.makeMeta()} and {@code ComponentEntity.fillMeta()}.
 * Moving this logic here keeps all IvoaXxx dependencies out of the entity
 * classes, which is a prerequisite for moving the entities to the
 * {@code calycopis-engine} module.
 */
public class ComponentMetadataAssembler
    {
    private ComponentMetadataAssembler()
        {
        }

    /**
     * Build an {@link IvoaComponentMetadata} bean from a
     * {@link ComponentEntity}.
     *
     * @param entity  the source entity
     * @param builder used to construct the {@code url} field
     * @return a populated IvoaComponentMetadata bean
     */
    public static IvoaComponentMetadata assemble(
        final ComponentEntity entity,
        final URIBuilder builder
        ){
        final IvoaComponentMetadata bean = new IvoaComponentMetadata();
        return fill(entity, builder, bean);
        }

    /**
     * Fill an existing {@link IvoaComponentMetadata} bean from a
     * {@link ComponentEntity}.
     *
     * @param entity  the source entity
     * @param builder used to construct the {@code url} field
     * @param bean    the bean to populate
     * @return the populated bean (same object as {@code bean})
     */
    public static IvoaComponentMetadata fill(
        final ComponentEntity entity,
        final URIBuilder builder,
        final IvoaComponentMetadata bean
        ){
        bean.setUuid(
            entity.getUuid()
            );
        bean.setUrl(
            builder.buildURI(
                entity.getWebappPath(),
                entity.getUuid()
                )
            );
        bean.setName(
            entity.getName()
            );
        bean.setCreated(
            entity.getCreated()
            );
        bean.setModified(
            entity.getModified()
            );
        bean.setMessages(
            entity.getMessageBeans()
            );
        return bean;
        }
    }
