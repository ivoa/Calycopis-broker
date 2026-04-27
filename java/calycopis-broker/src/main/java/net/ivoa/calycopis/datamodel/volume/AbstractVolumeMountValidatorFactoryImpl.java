/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
package net.ivoa.calycopis.datamodel.volume;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.validator.ValidatorFactoryImpl;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;

/**
 * A factory for volume mount validators.
 *
 */
@Slf4j
@Component
public class AbstractVolumeMountValidatorFactoryImpl
extends ValidatorFactoryImpl<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
implements AbstractVolumeMountValidatorFactory
    {

    public AbstractVolumeMountValidatorFactoryImpl()
        {
        super();
        }

    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractVolumeMount resource
        ){
        unknown(
            context,
            resource.getKind(),
            resource.getMeta()
            );
        }
    }
