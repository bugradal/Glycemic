import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { toast } from 'react-toastify'
import { Card, Grid, Image } from 'semantic-ui-react'
import SiteMenu from './components/SiteMenu'
import { ISingleFoods, ResultFoods } from './models/IFoods'
import { foodDetails } from './Services'
import { fncDateConvert } from './Util'

export default function Details() {

    const [food, setFood] = useState<ResultFoods>()

    const { url } = useParams()
    useEffect(() => {
        foodDetails(url!).then(res => {
            const dt: ISingleFoods = res.data;
            setFood(dt.result!)
        }).catch(err => {
            toast.dismiss();
            toast.error("" + err)
        })
    }, [])

    return (
        <>
            <SiteMenu />
            <Card centered style={{minWidth:'700px',marginTop:'50px'}} >
                <Card.Content>
                    {food?.image !== "" &&
                        <Image
                            floated='right'
                            size='small'
                            src={food?.image}
                        />
                    }

                    {food?.image === "" &&
                        <Image
                            floated='right'
                            size='small'
                            src='../foods.png'
                        />
                    }
                    <Card.Header >{food?.name} </Card.Header>
                    <Card.Meta style={{ marginTop: '30px' }}>Glisemik İndeks: {food?.glycemicIndex}</Card.Meta>
                    <Card.Meta style={{ marginTop: '10px' }}>Oluşturulan Kişi: {food?.createdBy === null ? 'user@mail.com' : food?.createdBy}</Card.Meta>
                    <Card.Meta style={{ marginTop: '10px' }}>Oluşturulma Tarihi: {fncDateConvert(food?.createdDate!)}</Card.Meta>
                </Card.Content>

            </Card>
        </>
    )
}
